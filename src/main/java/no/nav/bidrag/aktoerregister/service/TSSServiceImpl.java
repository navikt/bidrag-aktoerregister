package no.nav.bidrag.aktoerregister.service;

import static org.apache.commons.lang3.StringUtils.trimToNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import no.nav.bidrag.aktoerregister.domene.AdresseDTO;
import no.nav.bidrag.aktoerregister.domene.AktoerDTO;
import no.nav.bidrag.aktoerregister.domene.AktoerIdDTO;
import no.nav.bidrag.aktoerregister.domene.IdenttypeDTO;
import no.nav.bidrag.aktoerregister.domene.KontonummerDTO;
import no.nav.bidrag.aktoerregister.exception.AktoerNotFoundException;
import no.nav.bidrag.aktoerregister.exception.TSSServiceException;
import no.nav.bidrag.aktoerregister.properties.MQProperties;
import no.nav.bidrag.aktoerregister.service.mq.MQService;
import no.rtv.namespacetss.AdresseSamhType;
import no.rtv.namespacetss.KontoType;
import no.rtv.namespacetss.ObjectFactory;
import no.rtv.namespacetss.SamhAvdPraType;
import no.rtv.namespacetss.Samhandler;
import no.rtv.namespacetss.SamhandlerIDataB910Type;
import no.rtv.namespacetss.SamhandlerType;
import no.rtv.namespacetss.SvarStatusType;
import no.rtv.namespacetss.TServicerutiner;
import no.rtv.namespacetss.TidOFF1;
import no.rtv.namespacetss.TssSamhandlerData;
import no.rtv.namespacetss.TssSamhandlerData.TssInputData;
import no.rtv.namespacetss.TypeOD910;
import no.rtv.namespacetss.TypeSamhAdr;
import no.rtv.namespacetss.TypeSamhKonto;
import no.rtv.namespacetss.TypeSamhandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TSSServiceImpl implements AktoerService {

  public static final String ER_GYLDIG = "J";
  private final MQService mqService;

  private final MQProperties mqProperties;

  @Autowired
  public TSSServiceImpl(MQService mqService, MQProperties mqProperties) {
    this.mqService = mqService;
    this.mqProperties = mqProperties;
  }

  private static <I, O> Optional<O> getFirst(I input, Function<I, ? extends List<O>> transformer) {
    return input != null ? transformer.apply(input).stream().findFirst() : Optional.empty();
  }

  @Override
  public AktoerDTO hentAktoer(AktoerIdDTO aktoerId) {
    TssSamhandlerData request = createTssSamhandlerRequest(aktoerId);

    TssSamhandlerData response =
        mqService.performRequestResponse(
            mqProperties.getTssRequestQueue(),
            request,
            TssSamhandlerData.class,
            TssSamhandlerData.class);

    validerResponse(response, aktoerId.getAktoerId());
    return mapToAktoer(response, aktoerId);
  }

  private TssSamhandlerData createTssSamhandlerRequest(AktoerIdDTO aktoerId) {
    ObjectFactory objectFactory = new ObjectFactory();
    TServicerutiner servicerutiner = objectFactory.createTServicerutiner();

    SamhandlerIDataB910Type samhandlerIDataB910 = objectFactory.createSamhandlerIDataB910Type();
    if (aktoerId.getIdenttype().equals(IdenttypeDTO.AKTOERNUMMER)) {
      samhandlerIDataB910.setIdOffTSS(aktoerId.getAktoerId());
    } else {
      TidOFF1 tidOFF1 = objectFactory.createTidOFF1();
      tidOFF1.setIdOff(aktoerId.getAktoerId());
      tidOFF1.setKodeIdType("FNR");
      samhandlerIDataB910.setOFFid(tidOFF1);
    }
    samhandlerIDataB910.setHistorikk("N");
    samhandlerIDataB910.setBrukerID("RTV9999");
    servicerutiner.setSamhandlerIDataB910(samhandlerIDataB910);

    TssInputData tssInputData = objectFactory.createTssSamhandlerDataTssInputData();
    tssInputData.setTssServiceRutine(servicerutiner);
    TssSamhandlerData tssSamhandlerData = objectFactory.createTssSamhandlerData();
    tssSamhandlerData.setTssInputData(tssInputData);
    return tssSamhandlerData;
  }

  private AktoerDTO mapToAktoer(TssSamhandlerData tssSamhandlerData, AktoerIdDTO aktoerId) {
    Samhandler samhandler = hentSamhandler(tssSamhandlerData);
    if (samhandler != null) {
      String avdelingsnummer = hentAvdelingsnummer(aktoerId, samhandler);
      AktoerDTO aktoer = new AktoerDTO(aktoerId);
      aktoer.setOffentligId(hentOffentligId(samhandler));
      aktoer.setOffentligIdType(hentOffentligIdType(samhandler));
      aktoer.setAdresse(mapTilAdresse(samhandler, avdelingsnummer));
      aktoer.setKontonummer(mapTilKontonummer(samhandler, avdelingsnummer));
      return aktoer;
    }
    return null;
  }

  private Samhandler hentSamhandler(TssSamhandlerData tssSamhandlerData) {
    return getFirst(
            tssSamhandlerData.getTssOutputData().getSamhandlerODataB910(),
            TypeOD910::getEnkeltSamhandler)
        .orElse(null);
  }

  private String hentAvdelingsnummer(AktoerIdDTO aktoerId, Samhandler samhandler) {
    return samhandler.getSamhandlerAvd125().getSamhAvd().stream()
        .filter(avdeling -> Objects.equals(avdeling.getIdOffTSS(), aktoerId.getAktoerId()))
        .map(SamhAvdPraType::getAvdNr)
        .findFirst()
        .orElseThrow();
  }

  private String hentOffentligId(Samhandler samhandler) {
    return getFirst(samhandler.getSamhandler110(), TypeSamhandler::getSamhandler)
        .map(SamhandlerType::getIdOff)
        .orElse(null);
  }

  private String hentOffentligIdType(Samhandler samhandler) {
    return getFirst(samhandler.getSamhandler110(), TypeSamhandler::getSamhandler)
        .map(SamhandlerType::getKodeIdentType)
        .orElse(null);
  }

  private AdresseDTO mapTilAdresse(Samhandler samhandler, String avdelingsnummer) {
    TypeSamhAdr typeSamhAdr = samhandler.getAdresse130();
    if (typeSamhAdr != null) {
      AdresseDTO adresse = new AdresseDTO();
      settNavnFraSamhandler(samhandler, adresse);
      for (AdresseSamhType adresseSamhType : typeSamhAdr.getAdresseSamh()) {
        if (erGyldigOgHarRiktigAvdelingsnummer(
            avdelingsnummer, adresseSamhType.getGyldigAdresse(), adresseSamhType.getAvdNr())) {
          adresse.setLand(trimToNull(adresseSamhType.getKodeLand()));
          adresse.setPoststed(trimToNull(adresseSamhType.getPoststed()));
          adresse.setPostnr(trimToNull(adresseSamhType.getPostNr()));
          settAdresselinjer(adresse, adresseSamhType);
          return adresse;
        }
      }
    }
    return null;
  }

  private void settAdresselinjer(AdresseDTO adresse, AdresseSamhType adresseSamhType) {
    if (adresseSamhType.getAdrLinjeInfo() != null) {
      List<String> adresselinjer = adresseSamhType.getAdrLinjeInfo().getAdresseLinje();
      if (adresselinjer.size() >= 1) {
        adresse.setAdresselinje1(trimToNull(adresselinjer.get(0)));
      }
      if (adresselinjer.size() >= 2) {
        adresse.setAdresselinje2(trimToNull(adresselinjer.get(1)));
      }
      if (adresselinjer.size() >= 3) {
        adresse.setAdresselinje3(trimToNull(adresselinjer.get(2)));
      }
    }
  }

  private void settNavnFraSamhandler(Samhandler samhandler, AdresseDTO adresse) {
    getFirst(samhandler.getSamhandler110(), TypeSamhandler::getSamhandler)
        .map(SamhandlerType::getNavnSamh)
        .ifPresent(adresse::setNavn);
  }

  private KontonummerDTO mapTilKontonummer(Samhandler samhandler, String avdelingsnummer) {
    TypeSamhKonto typeSamhKonto = samhandler.getKonto140();
    if (typeSamhKonto != null) {
      for (KontoType kontoType : typeSamhKonto.getKonto()) {
        if (erGyldigOgHarRiktigAvdelingsnummer(
            avdelingsnummer, kontoType.getGyldigKonto(), kontoType.getAvdNr())) {
          KontonummerDTO kontonummer = new KontonummerDTO();
          kontonummer.setBankLandkode(trimToNull(kontoType.getKodeLand()));
          kontonummer.setBankNavn(trimToNull(kontoType.getBankNavn()));
          kontonummer.setNorskKontonr(trimToNull(kontoType.getGironrInnland()));
          kontonummer.setSwift(trimToNull(kontoType.getSwiftKode()));
          kontonummer.setValutaKode(trimToNull(kontoType.getKodeValuta()));
          kontonummer.setBankCode(trimToNull(kontoType.getBankKode()));
          kontonummer.setIban(trimToNull(kontoType.getGironrUtland()));
          return kontonummer;
        }
      }
    }
    return null;
  }

  private boolean erGyldigOgHarRiktigAvdelingsnummer(
      String avdelingsnummer, String gyldig, String avdNr) {
    return gyldig.equals(ER_GYLDIG) && avdNr.equals(avdelingsnummer);
  }

  private void validerResponse(TssSamhandlerData tssSamhandlerData, String aktoerId) {
    SvarStatusType svarStatusType = tssSamhandlerData.getTssOutputData().getSvarStatus();
    if (svarStatusType.getAlvorligGrad().equals("00")) {
      return;
    } else if (svarStatusType.getAlvorligGrad().equals("04")
        && svarStatusType.getKodeMelding().equals("B9XX008F")) {
      throw new AktoerNotFoundException("Aktoer med aktoerId (" + aktoerId + ") ikke funnet.");
    }
    throw new TSSServiceException(
        svarStatusType.getBeskrMelding() + " " + svarStatusType.getKodeMelding());
  }
}

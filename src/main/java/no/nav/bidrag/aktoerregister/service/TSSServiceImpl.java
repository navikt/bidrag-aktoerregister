package no.nav.bidrag.aktoerregister.service;

import static org.apache.commons.lang3.StringUtils.trimToNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import no.nav.bidrag.aktoerregister.exception.AktoerNotFoundException;
import no.nav.bidrag.aktoerregister.exception.TSSServiceException;
import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;
import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer.AktoerBuilder;
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
  public Aktoer hentAktoer(String aktoerIdent) {
    TssSamhandlerData request = opprettTssSamhandlerRequest(aktoerIdent);

    TssSamhandlerData tssSamhandlerData =
        mqService.performRequestResponse(
            mqProperties.getTssRequestQueue(),
            request,
            TssSamhandlerData.class,
            TssSamhandlerData.class);

    validerTssSamhandlerData(tssSamhandlerData, aktoerIdent);
    return mapTilAktoer(tssSamhandlerData, aktoerIdent);
  }

  private Aktoer mapTilAktoer(TssSamhandlerData tssSamhandlerData, String aktoerIdent) {
    Samhandler samhandler = hentSamhandler(tssSamhandlerData);
    String avdelingsnummer = hentAvdelingsnummer(aktoerIdent, samhandler);

    AktoerBuilder aktoerBuilder =
        Aktoer.builder()
            .aktoerIdent(aktoerIdent)
            .aktoerType("AKTOERNUMMER")
            .offentligId(hentOffentligId(samhandler))
            .offentligIdType(hentOffentligIdType(samhandler));

    mapSamhandlerAdresseTilAktoer(samhandler, avdelingsnummer, aktoerBuilder);
    mapSamhandlerKontonummerTilAktoer(samhandler, avdelingsnummer, aktoerBuilder);
    return aktoerBuilder.build();
  }

  private void mapSamhandlerAdresseTilAktoer(
      Samhandler samhandler, String avdelingsnummer, AktoerBuilder aktoerBuilder) {
    TypeSamhAdr typeSamhAdr = samhandler.getAdresse130();
    if (typeSamhAdr != null) {
      aktoerBuilder.navn(hentSamhandlerNavn(samhandler));

      for (AdresseSamhType adresseSamhType : typeSamhAdr.getAdresseSamh()) {
        if (erGyldigOgHarRiktigAvdelingsnummer(
            avdelingsnummer, adresseSamhType.getGyldigAdresse(), adresseSamhType.getAvdNr())) {
          aktoerBuilder.land(trimToNull(adresseSamhType.getKodeLand()));
          aktoerBuilder.poststed(trimToNull(adresseSamhType.getPoststed()));
          aktoerBuilder.postnr(trimToNull(adresseSamhType.getPostNr()));
          settAdresselinjer(aktoerBuilder, adresseSamhType);
        }
      }
    }
  }

  private void mapSamhandlerKontonummerTilAktoer(Samhandler samhandler, String avdelingsnummer,
      AktoerBuilder aktoerBuilder) {
    TypeSamhKonto typeSamhKonto = samhandler.getKonto140();
    if (typeSamhKonto != null) {
      for (KontoType kontoType : typeSamhKonto.getKonto()) {
        if (erGyldigOgHarRiktigAvdelingsnummer(
            avdelingsnummer, kontoType.getGyldigKonto(), kontoType.getAvdNr())) {
          aktoerBuilder.bankLandkode(trimToNull(kontoType.getKodeLand()));
          aktoerBuilder.bankNavn(trimToNull(kontoType.getBankNavn()));
          aktoerBuilder.norskKontonr(trimToNull(kontoType.getGironrInnland()));
          aktoerBuilder.swift(trimToNull(kontoType.getSwiftKode()));
          aktoerBuilder.valutaKode(trimToNull(kontoType.getKodeValuta()));
          aktoerBuilder.bankCode(trimToNull(kontoType.getBankKode()));
          aktoerBuilder.iban(trimToNull(kontoType.getGironrUtland()));
        }
      }
    }
  }

  private String hentSamhandlerNavn(Samhandler samhandler) {
    return samhandler.getSamhandler110().getSamhandler().stream()
        .findFirst()
        .map(SamhandlerType::getNavnSamh)
        .orElse(null);
  }

  private void settAdresselinjer(AktoerBuilder aktoer, AdresseSamhType adresseSamhType) {
    if (adresseSamhType.getAdrLinjeInfo() != null) {
      List<String> adresselinjer = adresseSamhType.getAdrLinjeInfo().getAdresseLinje();
      if (adresselinjer.size() >= 1) {
        aktoer.adresselinje1(trimToNull(adresselinjer.get(0)));
      }
      if (adresselinjer.size() >= 2) {
        aktoer.adresselinje2(trimToNull(adresselinjer.get(1)));
      }
      if (adresselinjer.size() >= 3) {
        aktoer.adresselinje3(trimToNull(adresselinjer.get(2)));
      }
    }
  }

  private TssSamhandlerData opprettTssSamhandlerRequest(String aktoerIdent) {
    ObjectFactory objectFactory = new ObjectFactory();
    TServicerutiner servicerutiner = objectFactory.createTServicerutiner();

    SamhandlerIDataB910Type samhandlerIDataB910 = objectFactory.createSamhandlerIDataB910Type();
    samhandlerIDataB910.setIdOffTSS(aktoerIdent);
    samhandlerIDataB910.setHistorikk("N");
    samhandlerIDataB910.setBrukerID("RTV9999");
    servicerutiner.setSamhandlerIDataB910(samhandlerIDataB910);

    TssInputData tssInputData = objectFactory.createTssSamhandlerDataTssInputData();
    tssInputData.setTssServiceRutine(servicerutiner);
    TssSamhandlerData tssSamhandlerData = objectFactory.createTssSamhandlerData();
    tssSamhandlerData.setTssInputData(tssInputData);
    return tssSamhandlerData;
  }

  private Samhandler hentSamhandler(TssSamhandlerData tssSamhandlerData) {
    return getFirst(
            tssSamhandlerData.getTssOutputData().getSamhandlerODataB910(),
            TypeOD910::getEnkeltSamhandler)
        .orElse(null);
  }

  private String hentAvdelingsnummer(String aktoerIdent, Samhandler samhandler) {
    return samhandler.getSamhandlerAvd125().getSamhAvd().stream()
        .filter(avdeling -> Objects.equals(avdeling.getIdOffTSS(), aktoerIdent))
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

  private boolean erGyldigOgHarRiktigAvdelingsnummer(
      String avdelingsnummer, String gyldig, String avdNr) {
    return gyldig.equals(ER_GYLDIG) && avdNr.equals(avdelingsnummer);
  }

  private void validerTssSamhandlerData(TssSamhandlerData tssSamhandlerData, String aktoerId) {
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

package no.nav.bidrag.aktoerregister.service;

import java.util.List;
import no.nav.bidrag.aktoerregister.domene.AdresseDTO;
import no.nav.bidrag.aktoerregister.domene.AktoerDTO;
import no.nav.bidrag.aktoerregister.domene.AktoerIdDTO;
import no.nav.bidrag.aktoerregister.domene.IdenttypeDTO;
import no.nav.bidrag.aktoerregister.domene.KontonummerDTO;
import no.nav.bidrag.aktoerregister.exception.AktoerNotFoundException;
import no.nav.bidrag.aktoerregister.exception.MQServiceException;
import no.nav.bidrag.aktoerregister.exception.TSSServiceException;
import no.nav.bidrag.aktoerregister.properties.MQProperties;
import no.nav.bidrag.aktoerregister.service.mq.MQService;
import no.rtv.namespacetss.AdresseSamhType;
import no.rtv.namespacetss.KontoType;
import no.rtv.namespacetss.ObjectFactory;
import no.rtv.namespacetss.Samhandler;
import no.rtv.namespacetss.SamhandlerIDataB910Type;
import no.rtv.namespacetss.SvarStatusType;
import no.rtv.namespacetss.TServicerutiner;
import no.rtv.namespacetss.TidOFF1;
import no.rtv.namespacetss.TssSamhandlerData;
import no.rtv.namespacetss.TssSamhandlerData.TssInputData;
import no.rtv.namespacetss.TypeOD910;
import no.rtv.namespacetss.TypeSamhAdr;
import no.rtv.namespacetss.TypeSamhKonto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TSSServiceImpl implements TSSService {

  private final MQService mqService;

  private final MQProperties mqProperties;

  @Autowired
  public TSSServiceImpl(MQService mqService, MQProperties mqProperties) {
    this.mqService = mqService;
    this.mqProperties = mqProperties;
  }

  @Override
  public AktoerDTO hentAktoer(AktoerIdDTO aktoerId) throws MQServiceException, AktoerNotFoundException, TSSServiceException {
    TssSamhandlerData request = createTssSamhandlerRequest(aktoerId);

    TssSamhandlerData response = mqService.performRequestResponse(mqProperties.getTssRequestQueue(), request, TssSamhandlerData.class, TssSamhandlerData.class);

    validateResponse(response, aktoerId.getAktoerId());
    return mapToAktoer(response, aktoerId);
  }

  private TssSamhandlerData createTssSamhandlerRequest(AktoerIdDTO aktoerId) {
    ObjectFactory objectFactory = new ObjectFactory();
    TServicerutiner servicerutiner = objectFactory.createTServicerutiner();

    SamhandlerIDataB910Type samhandlerIDataB910 = objectFactory.createSamhandlerIDataB910Type();
    if (aktoerId.getIdenttype().equals(IdenttypeDTO.AKTOERNUMMER)) {
      samhandlerIDataB910.setIdOffTSS(aktoerId.getAktoerId());
    }
    else {
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
    TypeOD910 samhandlerODataB910 = tssSamhandlerData.getTssOutputData().getSamhandlerODataB910();
    if (samhandlerODataB910 != null
      && samhandlerODataB910.getEnkeltSamhandler() != null
      && !samhandlerODataB910.getEnkeltSamhandler().isEmpty()) {

      Samhandler samhandler = samhandlerODataB910.getEnkeltSamhandler().get(0);

      AktoerDTO aktoer = new AktoerDTO(aktoerId);
      aktoer.setAdresse(mapToAdresse(samhandler));
      aktoer.setKontonummer(mapToKontonummer(samhandler));
      return aktoer;
    }
    return null;
  }

  private AdresseDTO mapToAdresse(Samhandler samhandler) {
    TypeSamhAdr typeSamhAdr = samhandler.getAdresse130();
    if (typeSamhAdr != null) {
      for (AdresseSamhType adresseSamhType : typeSamhAdr.getAdresseSamh()) {
        AdresseDTO adresse = new AdresseDTO();
        adresse.setLand(trim(adresseSamhType.getKodeLand()));
        adresse.setPoststed(trim(adresseSamhType.getPoststed()));
        adresse.setPostnr(trim(adresseSamhType.getPostNr()));
        if (adresseSamhType.getAdrLinjeInfo() != null) {
          List<String> adresselinjer = adresseSamhType.getAdrLinjeInfo().getAdresseLinje();
          if (adresselinjer.size() >= 1) {
            adresse.setAdresselinje1(trim(adresselinjer.get(0)));
          }
          if(adresselinjer.size() >= 2) {
            adresse.setAdresselinje2(trim(adresselinjer.get(1)));
          }
          if(adresselinjer.size() >= 3) {
            adresse.setAdresselinje3(trim(adresselinjer.get(2)));
          }
        }
        return adresse;
      }
    }
    return null;
  }

  private KontonummerDTO mapToKontonummer(Samhandler samhandler) {
    KontonummerDTO kontonummerNorsk = null;
    KontonummerDTO kontonummerUtlandsk = null;
    TypeSamhKonto typeSamhKonto = samhandler.getKonto140();
    if (typeSamhKonto != null) {
      for (KontoType kontoType : typeSamhKonto.getKonto()) {
        KontonummerDTO kontonummer = new KontonummerDTO();
        kontonummer.setBankLandkode(trim(kontoType.getKodeLand()));
        kontonummer.setBankNavn(trim(kontoType.getBankNavn()));
        kontonummer.setNorskKontonr(trim(kontoType.getGironrInnland()));
        kontonummer.setSwift(trim(kontoType.getSwiftKode()));
        kontonummer.setValutaKode(trim(kontoType.getKodeValuta()));
        kontonummer.setBankCode(trim(kontoType.getBankKode()));
        kontonummer.setIban(trim(kontoType.getGironrUtland()));
        if (kontonummer.getNorskKontonr() != null) {
          kontonummerNorsk = kontonummer;
        }
        if (kontonummer.getIban() != null) {
          kontonummerUtlandsk = kontonummer;
        }
      }
    }
    if (kontonummerNorsk != null) {
      return kontonummerNorsk;
    }
    return kontonummerUtlandsk;
  }

  private static String trim(String input) {
    return input != null && !input.isBlank()
      ? input.trim()
      : null;
  }

  private void validateResponse(TssSamhandlerData tssSamhandlerData, String aktoerId) throws AktoerNotFoundException, TSSServiceException {
    SvarStatusType svarStatusType = tssSamhandlerData.getTssOutputData().getSvarStatus();
    if (svarStatusType.getAlvorligGrad().equals("00")) {
      return;
    }
    else if (svarStatusType.getAlvorligGrad().equals("04") && svarStatusType.getKodeMelding().equals("B9XX008F")) {
      throw new AktoerNotFoundException("Aktoer med aktoerId (" + aktoerId + ") ikke funnet.");
    }
    throw new TSSServiceException(svarStatusType.getBeskrMelding() + " " + svarStatusType.getKodeMelding());
  }
}

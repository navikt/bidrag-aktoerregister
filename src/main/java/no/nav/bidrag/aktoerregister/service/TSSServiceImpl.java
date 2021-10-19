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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TSSServiceImpl implements TSSService {

  private final MQService mqService;

  private final MQProperties mqProperties;

  private static final Logger logger = LoggerFactory.getLogger(TSSService.class);

  @Autowired
  public TSSServiceImpl(MQService mqService, MQProperties mqProperties) {
    this.mqService = mqService;
    this.mqProperties = mqProperties;
  }

  @Override
  public AktoerDTO hentAktoer(AktoerIdDTO aktoerId) throws MQServiceException, AktoerNotFoundException, TSSServiceException {
    TssSamhandlerData request = createTssSamhandlerRequest(aktoerId);

    logger.info("Henter aktoer {} fra TSS.", aktoerId.getAktoerId());
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
    samhandlerIDataB910.setBrukerID("HMB2990");
    servicerutiner.setSamhandlerIDataB910(samhandlerIDataB910);

    TssInputData tssInputData = objectFactory.createTssSamhandlerDataTssInputData();
    tssInputData.setTssServiceRutine(servicerutiner);
    TssSamhandlerData tssSamhandlerData = objectFactory.createTssSamhandlerData();
    tssSamhandlerData.setTssInputData(tssInputData);
    return tssSamhandlerData;
  }

  private AktoerDTO mapToAktoer(TssSamhandlerData tssSamhandlerData, AktoerIdDTO aktoerId) {
    TypeOD910 samhandlerODataB910 = tssSamhandlerData.getTssOutputData().getSamhandlerODataB910();
    if (samhandlerODataB910 != null) {
      AdresseDTO adresse = mapToAdresse(samhandlerODataB910);
      KontonummerDTO kontonummer = mapToKontonummer(samhandlerODataB910);
      // Not storing TSS aktoer if adresse or kontonummer is null
      if (adresse != null & kontonummer != null) {
        AktoerDTO aktoer = new AktoerDTO(aktoerId);
        aktoer.setAdresse(mapToAdresse(samhandlerODataB910));
        aktoer.setKontonummer(mapToKontonummer(samhandlerODataB910));
        return aktoer;
      }
    }
    return null;
  }

  private AdresseDTO mapToAdresse(TypeOD910 samhandlerODataB910) {
    List<Samhandler> samhandlerListe = samhandlerODataB910.getEnkeltSamhandler();
    if (samhandlerListe.size() > 0) {
      TypeSamhAdr typeSamhAdr = samhandlerListe.get(0).getAdresse130();
      if (Integer.parseInt(typeSamhAdr.getAntAdresse()) > 0) {
        AdresseSamhType adresseSamhType = typeSamhAdr.getAdresseSamh().get(0);
        AdresseDTO adresse = new AdresseDTO();
        adresse.setLand(adresseSamhType.getKodeLand());
        adresse.setPoststed(adresseSamhType.getPoststed());
        adresse.setPostnr(adresseSamhType.getPostNr());
        List<String> adresselinjer = adresseSamhType.getAdrLinjeInfo().getAdresseLinje();
        if (adresselinjer.size() >= 1) {
          adresse.setAdresselinje1(adresselinjer.get(0));
        }
        if(adresselinjer.size() >= 2) {
          adresse.setAdresselinje2(adresselinjer.get(1));
        }
        if(adresselinjer.size() >= 3) {
          adresse.setAdresselinje3(adresselinjer.get(2));
        }
        return adresse;
      }
    }
    return null;
  }

  private KontonummerDTO mapToKontonummer(TypeOD910 samhandlerODataB910) {
    List<Samhandler> samhandlerListe = samhandlerODataB910.getEnkeltSamhandler();
    if (samhandlerListe.size() > 0) {
      TypeSamhKonto typeSamhKonto = samhandlerListe.get(0).getKonto140();
      if (Integer.parseInt(typeSamhKonto.getAntKonto()) > 0) {
        KontoType kontoType = typeSamhKonto.getKonto().get(0);
        KontonummerDTO kontonummer = new KontonummerDTO();
        kontonummer.setBankLandkode(kontoType.getKodeLand());
        kontonummer.setBankNavn(kontoType.getBankNavn());
        kontonummer.setNorskKontonr(kontoType.getGironrInnland());
        kontonummer.setSwift(kontoType.getSwiftKode());
        kontonummer.setValutaKode(kontoType.getKodeValuta());
        kontonummer.setIban(kontoType.getGironrUtland());
        return kontonummer;
      }
    }
    return null;
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

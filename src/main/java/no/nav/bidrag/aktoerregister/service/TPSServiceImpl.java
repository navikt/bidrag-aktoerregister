package no.nav.bidrag.aktoerregister.service;

import no.nav.bidrag.aktoerregister.domene.AktoerDTO;
import no.nav.bidrag.aktoerregister.domene.AktoerIdDTO;
import no.nav.bidrag.aktoerregister.domene.KontonummerDTO;
import no.nav.bidrag.aktoerregister.exception.AktoerNotFoundException;
import no.nav.bidrag.aktoerregister.exception.MQServiceException;
import no.nav.bidrag.aktoerregister.exception.TPSServiceException;
import no.nav.bidrag.aktoerregister.properties.MQProperties;
import no.nav.bidrag.aktoerregister.service.mq.MQService;
import no.rtv.namespacetps.ObjectFactory;
import no.rtv.namespacetps.PersondataFraTpsS102;
import no.rtv.namespacetps.SRnavn;
import no.rtv.namespacetps.StatusFraTPS;
import no.rtv.namespacetps.TgiroNrUtland;
import no.rtv.namespacetps.TgiroNummer;
import no.rtv.namespacetps.TpsPersonData;
import no.rtv.namespacetps.TpsPersonData.TpsServiceRutine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TPSServiceImpl implements TPSService {

  private final MQService mqService;

  private final MQProperties mqProperties;

  @Autowired
  public TPSServiceImpl(MQService mqService, MQProperties mqProperties) {
    this.mqService = mqService;
    this.mqProperties = mqProperties;
  }

  @Override
  public AktoerDTO hentAktoer(AktoerIdDTO aktoerId)
      throws MQServiceException, AktoerNotFoundException, TPSServiceException {
    TpsPersonData request = createTpsPersonDataRequest(aktoerId);

    TpsPersonData response =
        mqService.performRequestResponse(
            mqProperties.getTpsRequestQueue(), request, TpsPersonData.class, TpsPersonData.class);

    validateResponse(response, aktoerId.getAktoerId());
    return mapToAktoer(response, aktoerId);
  }

  private TpsPersonData createTpsPersonDataRequest(AktoerIdDTO aktoerId) {
    ObjectFactory objectFactory = new ObjectFactory();
    TpsPersonData tpsPersonData = objectFactory.createTpsPersonData();

    TpsServiceRutine tpsServiceRutine = objectFactory.createTpsPersonDataTpsServiceRutine();
    tpsServiceRutine.setServiceRutinenavn(SRnavn.FS_03_FDNUMMER_GIRONUMR_O);
    tpsServiceRutine.setFnr(aktoerId.getAktoerId());
    tpsServiceRutine.setAksjonsKode("A");
    tpsServiceRutine.setAksjonsKode2("0");

    tpsPersonData.setTpsServiceRutine(tpsServiceRutine);
    return tpsPersonData;
  }

  private AktoerDTO mapToAktoer(TpsPersonData tpsPersonData, AktoerIdDTO aktoerId) {
    PersondataFraTpsS102 persondataFraTpsS102 = tpsPersonData.getTpsSvar().getPersonDataS102();
    if (persondataFraTpsS102 != null) {

      AktoerDTO aktoer = new AktoerDTO(aktoerId);
      aktoer.setKontonummer(mapToKontonummer(persondataFraTpsS102));
      return aktoer;
    }
    return null;
  }

  private KontonummerDTO mapToKontonummer(PersondataFraTpsS102 persondataFraTpsS102) {
    TgiroNummer giroInfoNorsk = persondataFraTpsS102.getGiroInfoNorsk();
    TgiroNrUtland giroInfoUtlandsk = persondataFraTpsS102.getGiroInfoUtlandsk();

    if (giroInfoNorsk != null
        && giroInfoNorsk.getGiroNummer() != null
        && !giroInfoNorsk.getGiroNummer().isBlank()) {
      KontonummerDTO kontonummer = new KontonummerDTO();
      kontonummer.setNorskKontonr(trim(giroInfoNorsk.getGiroNummer()));
      return kontonummer;
    }
    if (giroInfoUtlandsk != null
        && giroInfoUtlandsk.getGiroNrUtland() != null
        && !giroInfoUtlandsk.getGiroNrUtland().isBlank()) {
      KontonummerDTO kontonummer = new KontonummerDTO();
      kontonummer.setIban(trim(giroInfoUtlandsk.getGiroNrUtland()));
      kontonummer.setSwift(trim(giroInfoUtlandsk.getSwiftKodeUtland()));
      kontonummer.setValutaKode(trim(giroInfoUtlandsk.getBankValuta()));
      kontonummer.setBankNavn(trim(giroInfoUtlandsk.getBankNavnUtland()));
      kontonummer.setBankLandkode(trim(giroInfoUtlandsk.getBankLandKode()));
      kontonummer.setBankCode(trim(giroInfoUtlandsk.getBankKodeUtland()));
      return kontonummer;
    }

    return null;
  }

  private static String trim(String input) {
    return input != null && !input.isBlank() ? input.trim() : null;
  }

  private void validateResponse(TpsPersonData tpsPersonData, String aktoerId)
      throws AktoerNotFoundException, TPSServiceException {
    StatusFraTPS statusFraTPS = tpsPersonData.getTpsSvar().getSvarStatus();
    String returStatus = statusFraTPS.getReturStatus();
    if (returStatus.equals("00") || returStatus.equals("04")) {
      return;
    } else if (returStatus.equals("08") && statusFraTPS.getReturMelding().equals("S102002F")) {
      throw new AktoerNotFoundException("Aktoer med aktoerId (" + aktoerId + ") ikke funnet.");
    }
    throw new TPSServiceException(
        statusFraTPS.getUtfyllendeMelding() + " " + statusFraTPS.getReturMelding());
  }
}

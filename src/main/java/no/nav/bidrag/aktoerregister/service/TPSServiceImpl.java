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
  public AktoerDTO hentAktoer(AktoerIdDTO aktoerId) throws MQServiceException, AktoerNotFoundException, TPSServiceException {
    TpsPersonData request = createTpsPersonDataRequest(aktoerId);
    TpsPersonData response = mqService.performRequestResponse(mqProperties.getTpsRequestQueue(), request, TpsPersonData.class, TpsPersonData.class);
    validateResponse(response, aktoerId.getAktoerId());
    return mapToAktoer(response, aktoerId);
  }

  @Override
  public TpsPersonData hentTpsPersonData(AktoerIdDTO aktoerId)
      throws MQServiceException {
    TpsPersonData request = createTpsPersonDataRequest(aktoerId);
    return mqService.performRequestResponse(mqProperties.getTpsRequestQueue(), request, TpsPersonData.class, TpsPersonData.class);
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
      TgiroNummer giroInfoNorsk = persondataFraTpsS102.getGiroInfoNorsk();
      TgiroNrUtland giroInfoUtlandsk = persondataFraTpsS102.getGiroInfoUtlandsk();

      AktoerDTO aktoer = new AktoerDTO(aktoerId);
      KontonummerDTO kontonummer = new KontonummerDTO();
      kontonummer.setNorskKontonr(giroInfoNorsk.getGiroNummer());
      kontonummer.setIban(giroInfoUtlandsk.getGiroNrUtland());
      kontonummer.setSwift(giroInfoUtlandsk.getSwiftKodeUtland());
      kontonummer.setValutaKode(giroInfoUtlandsk.getBankValuta());
      kontonummer.setBankNavn(giroInfoUtlandsk.getBankNavnUtland());
      kontonummer.setBankLandkode(giroInfoUtlandsk.getBankLandKode());
      aktoer.setKontonummer(kontonummer);
      return aktoer;
    }
    return null;
  }

  private void validateResponse(TpsPersonData tpsPersonData, String aktoerId) throws AktoerNotFoundException, TPSServiceException {
    StatusFraTPS statusFraTPS = tpsPersonData.getTpsSvar().getSvarStatus();
    if (statusFraTPS.getReturStatus().equals("00")) {
      return;
    } else if (statusFraTPS.getReturStatus().equals("08") && statusFraTPS.getReturMelding().equals("S102002F")) {
      throw new AktoerNotFoundException("Aktoer med aktoerId (" + aktoerId + ") ikke funnet.");
    }
    throw new TPSServiceException(statusFraTPS.getUtfyllendeMelding() + " " + statusFraTPS.getReturMelding());
  }
}

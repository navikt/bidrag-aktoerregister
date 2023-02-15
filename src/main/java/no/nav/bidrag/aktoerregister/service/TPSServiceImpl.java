package no.nav.bidrag.aktoerregister.service;

import static org.apache.commons.lang3.StringUtils.trimToNull;

import no.nav.bidrag.aktoerregister.exception.AktoerNotFoundException;
import no.nav.bidrag.aktoerregister.exception.TPSServiceException;
import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;
import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer.AktoerBuilder;
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
public class TPSServiceImpl implements AktoerService {

  private final MQService mqService;

  private final MQProperties mqProperties;

  @Autowired
  public TPSServiceImpl(MQService mqService, MQProperties mqProperties) {
    this.mqService = mqService;
    this.mqProperties = mqProperties;
  }

  @Override
  public Aktoer hentAktoer(String aktoerIdent) {
    TpsPersonData request = opprettTpsPersonDataRequest(aktoerIdent);

    TpsPersonData tpsPersonData =
        mqService.performRequestResponse(
            mqProperties.getTpsRequestQueue(), request, TpsPersonData.class, TpsPersonData.class);

    validerTpsPersonData(tpsPersonData, aktoerIdent);
    return mapTilAktoer(tpsPersonData, aktoerIdent);
  }

  private Aktoer mapTilAktoer(TpsPersonData tpsPersonData, String aktoerIdent) {
    AktoerBuilder aktoerBuilder =
        Aktoer.builder().aktoerIdent(aktoerIdent).aktoerType("PERSONNUMMER");

    PersondataFraTpsS102 persondataFraTpsS102 = tpsPersonData.getTpsSvar().getPersonDataS102();
    TgiroNummer giroInfoNorsk = persondataFraTpsS102.getGiroInfoNorsk();
    TgiroNrUtland giroInfoUtlandsk = persondataFraTpsS102.getGiroInfoUtlandsk();

    if (giroInfoNorsk != null && finnesGiroInfo(giroInfoNorsk.getGiroNummer()))
      byggNorsktKontonummer(aktoerBuilder, giroInfoNorsk);
    else if (giroInfoUtlandsk != null && finnesGiroInfo(giroInfoUtlandsk.getGiroNrUtland())) {
      byggUtenlandskKontonummer(aktoerBuilder, giroInfoUtlandsk);
    }
    return aktoerBuilder.build();
  }

  private void byggNorsktKontonummer(AktoerBuilder aktoerBuilder, TgiroNummer giroInfoNorsk) {
    aktoerBuilder.norskKontonr(trimToNull(giroInfoNorsk.getGiroNummer()));
  }

  private void byggUtenlandskKontonummer(
      AktoerBuilder aktoerBuilder, TgiroNrUtland giroInfoUtlandsk) {
    aktoerBuilder
        .iban(trimToNull(giroInfoUtlandsk.getGiroNrUtland()))
        .swift(trimToNull(giroInfoUtlandsk.getSwiftKodeUtland()))
        .valutaKode(trimToNull(giroInfoUtlandsk.getBankValuta()))
        .bankNavn(trimToNull(giroInfoUtlandsk.getBankNavnUtland()))
        .bankLandkode(trimToNull(giroInfoUtlandsk.getBankLandKode()))
        .bankCode(trimToNull(giroInfoUtlandsk.getBankKodeUtland()));
  }

  private TpsPersonData opprettTpsPersonDataRequest(String aktoerIdent) {
    ObjectFactory objectFactory = new ObjectFactory();
    TpsPersonData tpsPersonData = objectFactory.createTpsPersonData();

    TpsServiceRutine tpsServiceRutine = objectFactory.createTpsPersonDataTpsServiceRutine();
    tpsServiceRutine.setServiceRutinenavn(SRnavn.FS_03_FDNUMMER_GIRONUMR_O);
    tpsServiceRutine.setFnr(aktoerIdent);
    tpsServiceRutine.setAksjonsKode("A");
    tpsServiceRutine.setAksjonsKode2("0");

    tpsPersonData.setTpsServiceRutine(tpsServiceRutine);
    return tpsPersonData;
  }

  private boolean finnesGiroInfo(String giroInfoNorsk1) {
    return giroInfoNorsk1 != null && !giroInfoNorsk1.isBlank();
  }

  private void validerTpsPersonData(TpsPersonData tpsPersonData, String aktoerId) {
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

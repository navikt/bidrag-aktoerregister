package no.nav.bidrag.aktoerregister.service;

import jakarta.xml.bind.JAXBException;
import java.util.concurrent.TimeoutException;
import javax.jms.JMSException;
import no.nav.bidrag.aktoerregister.domene.Aktoer;
import no.nav.bidrag.aktoerregister.domene.AktoerId;
import no.nav.bidrag.aktoerregister.domene.Kontonummer;
import no.nav.bidrag.aktoerregister.properties.MQProperties;
import no.nav.bidrag.aktoerregister.service.mq.MQService;
import no.rtv.namespacetps.ObjectFactory;
import no.rtv.namespacetps.PersondataFraTpsS102;
import no.rtv.namespacetps.SRnavn;
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
  public Aktoer hentKontoInfo(AktoerId aktoerId) throws JAXBException, JMSException, TimeoutException, NullPointerException {
    TpsPersonData request = createTpsPersonDataRequest(aktoerId);
    return mapToAktoer(mqService.performRequestResponse(mqProperties.getTpsRequestQueue(), request, TpsPersonData.class, TpsPersonData.class), aktoerId);
  }

  private TpsPersonData createTpsPersonDataRequest(AktoerId aktoerId) {
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

  private Aktoer mapToAktoer(TpsPersonData tpsPersonData, AktoerId aktoerId) throws NullPointerException {
    Aktoer aktoer = new Aktoer(aktoerId);
    PersondataFraTpsS102 persondataFraTpsS102 = tpsPersonData.getTpsSvar().getPersonDataS102();
    TgiroNummer giroInfoNorsk = persondataFraTpsS102.getGiroInfoNorsk();
    TgiroNrUtland giroInfoUtlandsk = persondataFraTpsS102.getGiroInfoUtlandsk();
    Kontonummer kontonummer = new Kontonummer();
    kontonummer.setNorskKontonr(giroInfoNorsk.getGiroNummer());
    kontonummer.setIban(giroInfoUtlandsk.getGiroNrUtland());
    kontonummer.setSwift(giroInfoUtlandsk.getSwiftKodeUtland());
    kontonummer.setValutaKode(giroInfoUtlandsk.getBankValuta());
    kontonummer.setBankNavn(giroInfoUtlandsk.getBankNavnUtland());
    kontonummer.setBankLandkode(giroInfoUtlandsk.getBankLandKode());
    aktoer.setKontonummer(kontonummer);
    return aktoer;
  }
}

package no.nav.bidrag.aktoerregister.service;

import jakarta.xml.bind.JAXBException;
import javax.jms.JMSException;
import no.nav.bidrag.aktoerregister.domene.AktoerId;
import no.nav.bidrag.aktoerregister.properties.MQProperties;
import no.rtv.namespacetps.ObjectFactory;
import no.rtv.namespacetps.SRnavn;
import no.rtv.namespacetps.TpsPersonData;
import no.rtv.namespacetps.TpsPersonData.TpsServiceRutine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TPSTestServiceImpl implements TPSTestService {

  private final MQService mqService;

  private final MQProperties mqProperties;

  @Autowired
  public TPSTestServiceImpl(MQService mqService, MQProperties mqProperties) {
    this.mqService = mqService;
    this.mqProperties = mqProperties;
  }

  @Override
  public TpsPersonData hentKontoInfo(AktoerId aktoerId) throws JAXBException, JMSException {
    TpsPersonData request = createTpsPersonDataRequest(aktoerId);
    return mqService.performRequestResponse(mqProperties.getTpsRequestQueue(), request, TpsPersonData.class, TpsPersonData.class);
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
}

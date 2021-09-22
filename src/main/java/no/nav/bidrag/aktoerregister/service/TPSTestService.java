package no.nav.bidrag.aktoerregister.service;

import jakarta.xml.bind.JAXBException;
import javax.jms.JMSException;
import no.nav.bidrag.aktoerregister.domene.AktoerId;
import no.rtv.namespacetps.TpsPersonData;

public interface TPSTestService {

  TpsPersonData hentKontoInfo(AktoerId aktoerId) throws JAXBException, JMSException;
}

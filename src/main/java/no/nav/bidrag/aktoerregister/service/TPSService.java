package no.nav.bidrag.aktoerregister.service;

import jakarta.xml.bind.JAXBException;
import java.util.concurrent.TimeoutException;
import javax.jms.JMSException;
import no.nav.bidrag.aktoerregister.domene.Aktoer;
import no.nav.bidrag.aktoerregister.domene.AktoerId;
import no.rtv.namespacetps.TpsPersonData;

public interface TPSService {

  Aktoer hentKontoInfo(AktoerId aktoerId) throws JAXBException, JMSException, TimeoutException, NullPointerException;

  TpsPersonData hentTpsPersonData(AktoerId aktoerId) throws JAXBException, JMSException, TimeoutException, NullPointerException;

}

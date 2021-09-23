package no.nav.bidrag.aktoerregister.service;

import jakarta.xml.bind.JAXBException;
import java.util.concurrent.TimeoutException;
import javax.jms.JMSException;
import no.nav.bidrag.aktoerregister.domene.Aktoer;
import no.nav.bidrag.aktoerregister.domene.AktoerId;
import no.rtv.namespacetss.TssSamhandlerData;

public interface TSSTestService {

  Aktoer hentAktoer(AktoerId aktoerId) throws JAXBException, JMSException, TimeoutException;

  TssSamhandlerData hentTssSamhandler(AktoerId aktoerId) throws JAXBException, JMSException, TimeoutException;
}

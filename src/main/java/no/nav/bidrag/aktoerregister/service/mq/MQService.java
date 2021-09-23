package no.nav.bidrag.aktoerregister.service.mq;

import jakarta.xml.bind.JAXBException;
import java.util.concurrent.TimeoutException;
import javax.jms.JMSException;

public interface MQService {
  <Request, Response> Response performRequestResponse(String queue, Request request, Class<Request> requestClass, Class<Response> responseClass)
      throws JAXBException, JMSException, TimeoutException;

  <Response> void consume(MQMessageHandler<Response> callback, String queue, Class<Response> responseClass)
      throws JMSException, JAXBException;
}

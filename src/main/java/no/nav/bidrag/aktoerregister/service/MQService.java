package no.nav.bidrag.aktoerregister.service;

import jakarta.xml.bind.JAXBException;
import javax.jms.JMSException;

public interface MQService {
  <Request, Response> Response performRequestResponse(String queue, Request request, Class<Request> requestClass, Class<Response> responseClass)
      throws JAXBException, JMSException;
}

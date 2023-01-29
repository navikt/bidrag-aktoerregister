package no.nav.bidrag.aktoerregister.service.mq;

import no.nav.bidrag.aktoerregister.exception.MQServiceException;

public interface MQService {
  <Request, Response> Response performRequestResponse(
      String queue, Request request, Class<Request> requestClass, Class<Response> responseClass)
      throws MQServiceException;
}

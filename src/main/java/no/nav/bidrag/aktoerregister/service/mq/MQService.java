package no.nav.bidrag.aktoerregister.service.mq;

public interface MQService {
  <Request, Response> Response performRequestResponse(
      String queue, Request request, Class<Request> requestClass, Class<Response> responseClass);
}

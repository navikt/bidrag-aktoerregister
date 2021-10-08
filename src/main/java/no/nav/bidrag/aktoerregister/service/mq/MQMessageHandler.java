package no.nav.bidrag.aktoerregister.service.mq;

public interface MQMessageHandler<Response> {
  void onMessage(Response response);
}

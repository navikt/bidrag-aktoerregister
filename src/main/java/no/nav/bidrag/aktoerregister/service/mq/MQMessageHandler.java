package no.nav.bidrag.aktoerregister.service.mq;

public interface MQMessageHandler<Response> {
  boolean onMessage(Response response);
}

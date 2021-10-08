package no.nav.bidrag.aktoerregister.service.mq;

import static no.nav.bidrag.aktoerregister.util.XmlUtil.getObjectFromXMLMessage;

import javax.jms.Message;
import no.rtv.namespacetps.DistribusjonsMelding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class TPSConsumer {

  private final Logger logger = LoggerFactory.getLogger(TPSConsumer.class);

  private final MQMessageHandler<DistribusjonsMelding> tpsMessageHandler;

  @Autowired
  public TPSConsumer(MQMessageHandler<DistribusjonsMelding> tpsMessageHandler) {
    this.tpsMessageHandler = tpsMessageHandler;
  }

  @JmsListener(destination = "${mq.tpsEventQueue}", containerFactory = "tpsContainerFactory")
  public void receiveMessage(Message message) throws Exception {
    if (message != null) {
      try {
        tpsMessageHandler.onMessage(getObjectFromXMLMessage(message.getBody(String.class), DistribusjonsMelding.class));
        message.acknowledge();
      } catch (Exception e) {
        logger.error("Something went wrong in the processing of the message. Message will not be acknowledged.", e);
        throw e;
      }
    }
  }
}

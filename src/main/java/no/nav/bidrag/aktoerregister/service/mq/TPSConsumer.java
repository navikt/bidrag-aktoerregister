package no.nav.bidrag.aktoerregister.service.mq;

import static no.nav.bidrag.aktoerregister.util.XmlUtil.getObjectFromXMLMessage;

import io.swagger.v3.core.util.Json;
import jakarta.xml.bind.JAXBException;
import javax.jms.JMSException;
import javax.jms.Message;
import no.nav.bidrag.aktoerregister.util.JsonUtil;
import no.rtv.namespacetps.DistribusjonsMelding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class TPSConsumer {

  private final Logger logger = LoggerFactory.getLogger(TPSConsumer.class);

  @JmsListener(destination = "${mq.tpsEventQueue}", containerFactory = "myFactory")
  public void receiveMessage(Message message) throws JMSException, JAXBException {
    logger.info(message.getBody(String.class));
    DistribusjonsMelding distribusjonsMelding = getObjectFromXMLMessage(message.getBody(String.class), DistribusjonsMelding.class);
    logger.info("Received message: {}", JsonUtil.objectToJsonString(distribusjonsMelding));
  }
}

package no.nav.bidrag.aktoerregister.service.mq;

import static no.nav.bidrag.aktoerregister.util.XmlUtil.getObjectFromXMLMessage;

import jakarta.xml.bind.JAXBException;
import javax.jms.JMSException;
import javax.jms.Message;
import no.rtv.namespacetps.DistribusjonsMelding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

//@Component
//public class TPSConsumer {
//
//  private final MQMessageHandler<DistribusjonsMelding> tpsMessageHandler;
//
//  @Autowired
//  public TPSConsumer(MQMessageHandler<DistribusjonsMelding> tpsMessageHandler) {
//    this.tpsMessageHandler = tpsMessageHandler;
//  }
//
//  @JmsListener(destination = "${mq.tpsEventQueue}", containerFactory = "tpsContainerFactory")
//  public void receiveMessage(Message message) throws JMSException, JAXBException {
//    if (message != null) {
//      tpsMessageHandler.onMessage(
//          getObjectFromXMLMessage(message.getBody(String.class), DistribusjonsMelding.class));
//    }
//  }
//}

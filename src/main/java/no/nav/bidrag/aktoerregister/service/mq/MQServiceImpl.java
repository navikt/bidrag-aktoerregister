package no.nav.bidrag.aktoerregister.service.mq;

import static no.nav.bidrag.aktoerregister.util.XmlUtil.*;

import jakarta.xml.bind.JAXBException;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.TextMessage;
import no.nav.bidrag.aktoerregister.exception.MQServiceException;
import no.nav.bidrag.aktoerregister.properties.MQProperties;
import no.nav.bidrag.aktoerregister.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQServiceImpl implements MQService {

  private final MQProperties mqProperties;

  private final ConnectionFactory connectionFactory;

  @Autowired
  public MQServiceImpl(MQProperties mqProperties, ConnectionFactory connectionFactory) {
    this.mqProperties = mqProperties;
    this.connectionFactory = connectionFactory;
  }

  @Override
  public <Request, Response> Response performRequestResponse(
      String queue, Request request, Class<Request> requestClass, Class<Response> responseClass) {
    try {
      JMSContext jmsContext = connectionFactory.createContext();

      Destination requestQueue = jmsContext.createQueue(queue);

      TextMessage requestMessage =
          jmsContext.createTextMessage(createXMLString(request, requestClass));

      // Creating temporary queue and asking for the reply to be written to that queue
      Destination responseQueue = jmsContext.createTemporaryQueue();
      requestMessage.setJMSReplyTo(responseQueue);

      // Sending the message to the request queue
      sendMessage(jmsContext, requestMessage, requestQueue);

      // Waiting for and consuming response from response queue
      Message responseMessage = consumeMessage(jmsContext, responseQueue);

      jmsContext.close();
      if (responseMessage == null) {
        throw new MQServiceException("Konsument timet ut uten å ha mottatt noen respons fra MQ");
      }

      // Mapping response message to Response class.
      return getObjectFromXMLMessage(responseMessage.getBody(String.class), responseClass);
    } catch (JMSException | JAXBException | MQServiceException e) {
      throw new MQServiceException(
          "MQ Request-Response feilet mot kø: "
              + queue
              + " med request: "
              + JsonUtil.objectToJsonString(request),
          e);
    }
  }

  private void sendMessage(JMSContext context, Message message, Destination queue) {
    JMSProducer producer = context.createProducer();
    producer.send(queue, message);
  }

  private Message consumeMessage(JMSContext context, Destination queue) {
    JMSConsumer consumer = context.createConsumer(queue);
    return consumer.receive(mqProperties.getTimeout());
  }
}

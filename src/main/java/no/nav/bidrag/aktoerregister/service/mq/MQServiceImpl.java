package no.nav.bidrag.aktoerregister.service.mq;

import static no.nav.bidrag.aktoerregister.util.XmlUtil.*;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQServiceImpl implements MQService {

  private final MQProperties mqProperties;

  private final ConnectionFactory connectionFactory;

  private static final Logger logger = LoggerFactory.getLogger(MQService.class);

  @Autowired
  public MQServiceImpl(MQProperties mqProperties, ConnectionFactory connectionFactory) {
    this.mqProperties = mqProperties;
    this.connectionFactory = connectionFactory;
  }

  @Override
  public <Request, Response> Response performRequestResponse(String queue, Request request, Class<Request> requestClass,
      Class<Response> responseClass)
      throws MQServiceException {
    try {

//      JMSContext jmsContext = createMQContext(true);

      JMSContext jmsContext = connectionFactory.createContext();

      Destination requestQueue = jmsContext.createQueue(queue);

      TextMessage requestMessage = jmsContext.createTextMessage(createXMLString(request, requestClass));

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
      throw new MQServiceException("MQ Request-Response feilet mot kø: " + queue + " med request: " + JsonUtil.objectToJsonString(request), e);
    }
  }

  public <Response> void consume(MQMessageHandler<Response> messageHandler, String queue, Class<Response> responseClass)
      throws MQServiceException {
    try {
//      JMSContext jmsContext = createMQContext();
      JMSContext jmsContext = connectionFactory.createContext(JMSContext.CLIENT_ACKNOWLEDGE);
      jmsContext.recover();
      Destination consumtionQueue = jmsContext.createQueue(queue);
      boolean run = true;
      int nrFailedAttempts = 0;
      while (run) {
        Message message = consumeMessage(jmsContext, consumtionQueue);
        if (message == null) {
          continue;
        }
        boolean success = messageHandler.onMessage(getObjectFromXMLMessage(message.getBody(String.class), responseClass));
        if (success) {
          // Resetting nr of failed message handling attempts
          nrFailedAttempts = 0;
          // Manually acknowledging that we have processed and are done with the message
          message.acknowledge();
        } else {
          // When message handling has failed 5 times we stop the consumer
          nrFailedAttempts++;
          if (nrFailedAttempts >= 5) {
            run = false;
          }
        }
      }
      jmsContext.close();
    } catch (JMSException | JAXBException e) {
      throw new MQServiceException("MQ-konsument for kø: " + queue + " har feilet.", e);
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

//  private JMSContext createMQContext() throws JMSException {
//    return createMQContext(false);
//  }

//  private JMSContext createMQContext(boolean autoAcknowledge) throws JMSException {
//    try {
//      // Create a connection factory
//      JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
//      JmsConnectionFactory cf = ff.createConnectionFactory();
//
//      // Set the properties
//      cf.setStringProperty(WMQConstants.WMQ_HOST_NAME, mqProperties.getHost());
//      cf.setIntProperty(WMQConstants.WMQ_PORT, mqProperties.getPort());
//      cf.setStringProperty(WMQConstants.WMQ_CHANNEL, mqProperties.getChannel());
//      cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
//      cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, mqProperties.getQueueManager());
//      cf.setStringProperty(WMQConstants.WMQ_APPLICATIONNAME, mqProperties.getApplicationName());
//      cf.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, true);
//      cf.setStringProperty(WMQConstants.USERID, mqProperties.getUsername());
//      cf.setStringProperty(WMQConstants.PASSWORD, mqProperties.getPassword());
//
//      if (autoAcknowledge) {
//        return cf.createContext(JMSContext.AUTO_ACKNOWLEDGE);
//      }
//
//      return cf.createContext(JMSContext.CLIENT_ACKNOWLEDGE);
//    } catch (JMSException e) {
//      logger.error("Failed while setting up MQ connection.");
//      throw e;
//    }
//  }
}

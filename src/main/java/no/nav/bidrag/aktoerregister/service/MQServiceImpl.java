package no.nav.bidrag.aktoerregister.service;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.TextMessage;
import no.nav.bidrag.aktoerregister.properties.MQProperties;
import no.nav.bidrag.aktoerregister.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQServiceImpl implements MQService {

  @Autowired
  private MQProperties mqProperties;

  private static final Logger logger = LoggerFactory.getLogger(MQService.class);

  @Override
  public <Request, Response> Response performRequestResponse(String queue, Request request, Class<Request> requestClass, Class<Response> responseClass)
      throws JMSException, JAXBException {
    try {
      JMSContext jmsContext = createMQContext();
      Destination requestQueue = jmsContext.createQueue(queue);

      TextMessage requestMessage = jmsContext.createTextMessage(createXMLString(request, requestClass));

      // Creating temporary queue and asking for the reply to be written to that queue
      Destination responseQueue = jmsContext.createTemporaryQueue();
      requestMessage.setJMSReplyTo(responseQueue);

      // Sending the message to the request queue
      sendMessage(jmsContext, requestMessage, requestQueue);

      // Waiting for and consuming response from response queue
      Message responseMessage = consumeMessage(jmsContext, responseQueue);

      // TODO: Check if responseMessage is null. This can happen if 15 seconds go by with no response.

      // Mapping response message to Response class.
      return getObjectFromXMLMessage(responseMessage, responseClass);
    } catch (JMSException | JAXBException e) {
      throw e;
    }
  }

  private <T> String createXMLString(T object, Class<T> objectType) throws JAXBException {
    JAXBContext jaxbContext = JAXBContext.newInstance(objectType);
    Marshaller marshaller = jaxbContext.createMarshaller();
    StringWriter stringWriter = new StringWriter();
    marshaller.marshal(object, stringWriter);
    return stringWriter.toString();
  }

  private <T> T getObjectFromXMLMessage(Message message, Class<T> objectType) throws JAXBException, JMSException {
    JAXBContext jaxbContext = JAXBContext.newInstance(objectType);
    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
    StringReader result = new StringReader(message.getBody(String.class));
    return (T) unmarshaller.unmarshal(result);
  }

  private void sendMessage(JMSContext context, Message message, Destination queue) {
    JMSProducer producer = context.createProducer();
    producer.send(queue, message);
  }

  private Message consumeMessage(JMSContext context, Destination queue) {
    JMSConsumer consumer = context.createConsumer(queue);
    return consumer.receive(mqProperties.getTimeout());
  }

  private JMSContext createMQContext() throws JMSException {
    try {
      // Create a connection factory
      JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
      JmsConnectionFactory cf = ff.createConnectionFactory();

      logger.info("MQProperties: {}", JsonUtil.objectToJsonString(mqProperties));

      // Set the properties
      cf.setStringProperty(WMQConstants.WMQ_HOST_NAME, mqProperties.getHost());
      cf.setIntProperty(WMQConstants.WMQ_PORT, mqProperties.getPort());
      cf.setStringProperty(WMQConstants.WMQ_CHANNEL, mqProperties.getChannel());
      cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
      cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, mqProperties.getQueueManager());
      cf.setStringProperty(WMQConstants.WMQ_APPLICATIONNAME, "JmsPutGet (JMS)");
      cf.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, true);
      cf.setStringProperty(WMQConstants.USERID, mqProperties.getUsername());
      cf.setStringProperty(WMQConstants.PASSWORD, mqProperties.getPassword());

      return cf.createContext();
    } catch (JMSException e) {
      logger.error("Failed while setting up MQ connection. " + "Message: " + e.getMessage() + " StackTrace: " + Arrays.toString(e.getStackTrace()));
      throw e;
    }
  }
}

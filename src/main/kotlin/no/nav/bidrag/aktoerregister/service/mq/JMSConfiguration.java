package no.nav.bidrag.aktoerregister.service.mq;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;
import no.nav.bidrag.aktoerregister.properties.MQProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.util.backoff.ExponentialBackOff;

@Configuration
@EnableJms
public class JMSConfiguration {

  private final MQProperties mqProperties;

  @Autowired
  public JMSConfiguration(MQProperties mqProperties) {
    this.mqProperties = mqProperties;
  }

  @Bean
  public ConnectionFactory connectionFactory() throws JMSException {
    JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
    JmsConnectionFactory cf = ff.createConnectionFactory();
    // Set the properties
    cf.setStringProperty(WMQConstants.WMQ_HOST_NAME, mqProperties.getHost());
    cf.setIntProperty(WMQConstants.WMQ_PORT, mqProperties.getPort());
    cf.setStringProperty(WMQConstants.WMQ_CHANNEL, mqProperties.getChannel());
    cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
    cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, mqProperties.getQueueManager());
    cf.setStringProperty(WMQConstants.WMQ_APPLICATIONNAME, mqProperties.getApplicationName());
    cf.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, true);
    cf.setStringProperty(WMQConstants.USERID, mqProperties.getUsername());
    cf.setStringProperty(WMQConstants.PASSWORD, mqProperties.getPassword());
    return cf;
  }

  @Bean
  public JmsListenerContainerFactory<?> tpsContainerFactory(
      ConnectionFactory connectionFactory,
      DefaultJmsListenerContainerFactoryConfigurer configurer) {
    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    factory.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
    factory.setSessionTransacted(true);
    factory.setErrorHandler(new TPSConsumerErrorHandler());
    factory.setExceptionListener(new TPSExceptionListener());
    ExponentialBackOff exponentialBackOff = new ExponentialBackOff();
    exponentialBackOff.setInitialInterval(mqProperties.getBackOffInitialInterval());
    exponentialBackOff.setMaxInterval(mqProperties.getBackOffMaxInterval());
    factory.setBackOff(exponentialBackOff);
    configurer.configure(factory, connectionFactory);
    return factory;
  }
}

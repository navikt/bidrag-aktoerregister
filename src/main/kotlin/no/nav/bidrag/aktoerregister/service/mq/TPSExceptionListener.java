package no.nav.bidrag.aktoerregister.service.mq;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TPSExceptionListener implements ExceptionListener {

  private final Logger logger = LoggerFactory.getLogger("TPSExceptionListener");

  @Override
  public void onException(JMSException e) {
    logger.warn(e.getMessage(), e);
  }
}

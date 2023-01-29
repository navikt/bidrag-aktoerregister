package no.nav.bidrag.aktoerregister.service.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ErrorHandler;

public class TPSConsumerErrorHandler implements ErrorHandler {

  private final Logger logger = LoggerFactory.getLogger(TPSConsumerErrorHandler.class);

  @Override
  public void handleError(Throwable throwable) {
    logger.error(
        "Something went wrong in the processing of the message. Message will not be acknowledged.",
        throwable);
  }
}

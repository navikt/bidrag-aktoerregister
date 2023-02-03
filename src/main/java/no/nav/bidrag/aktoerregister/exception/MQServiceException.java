package no.nav.bidrag.aktoerregister.exception;

public class MQServiceException extends RuntimeException {

  public MQServiceException(String message) {
    super(message);
  }

  public MQServiceException(String message, Throwable error) {
    super(message, error);
  }
}

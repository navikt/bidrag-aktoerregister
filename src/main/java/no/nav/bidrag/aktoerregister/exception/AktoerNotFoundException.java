package no.nav.bidrag.aktoerregister.exception;

public class AktoerNotFoundException extends Exception {

  public AktoerNotFoundException(String message) {
    super(message);
  }

  public AktoerNotFoundException(String message, Throwable error) {
    super(message, error);
  }
}

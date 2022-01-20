package no.nav.bidrag.aktoerregister.exception;

import java.util.List;
import no.nav.bidrag.aktoerregister.service.graphql.GraphQLResponse.Error;

public class PDLServiceException extends Exception {

  private String message;

  public PDLServiceException(String message) {
    this.message = message;
  }

  public PDLServiceException(List<Error> errors) throws AktoerNotFoundException {
    StringBuilder sb = new StringBuilder();
    for (Error error : errors) {
      switch (error.getExtensions().getCode()) {
        case UNAUTHENTICATED, UNAUTHORIZED, BAD_REQUEST, SERVER_ERROR -> sb.append(buildErrorMessage(error));
        case NOT_FOUND -> throw new AktoerNotFoundException(buildErrorMessage(error));
      }
      message = sb.toString();
    }
  }

  public PDLServiceException(String message, Throwable error) {
    super(message, error);
    this.message = message;
  }

  @Override
  public String getMessage() {
    return message;
  }

  private String buildErrorMessage(Error error) {
    return error.getMessage() + ". Code: " + error.getExtensions().getCode() + " " + error.getExtensions().getClassification();
  }
}

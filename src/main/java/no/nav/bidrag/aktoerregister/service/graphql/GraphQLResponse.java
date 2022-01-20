package no.nav.bidrag.aktoerregister.service.graphql;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;
import lombok.Data;
import no.nav.bidrag.aktoerregister.domene.PersonDTO;

@Data
public class GraphQLResponse {
  private Data data;
  private List<Error> errors;

  @lombok.Data
  public static class Data {
    private PersonDTO hentPerson;
  }

  @lombok.Data
  public static class Error {
    private String message;
    private List<Location> locations;
    private List<String> path;
    private Extension extensions;
  }

  @lombok.Data
  public static class Location {
    private int line;
    private int column;
  }

  @lombok.Data
  public static class Extension {
    private ErrorCode code;
    private String classification;
  }

  public enum ErrorCode {
    UNAUTHENTICATED("unauthenticated"),
    UNAUTHORIZED("unauthorized"),
    NOT_FOUND("not_found"),
    BAD_REQUEST("bad_request"),
    SERVER_ERROR("server_error");

    private final String value;

    ErrorCode(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }
  }
}

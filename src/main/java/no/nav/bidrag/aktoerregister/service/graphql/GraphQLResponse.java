package no.nav.bidrag.aktoerregister.service.graphql;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import lombok.Data;
import no.nav.bidrag.aktoerregister.domene.PersonDTO;

@Data
public class GraphQLResponse {
  private Data data;
  private List<JsonNode> errors;

  @lombok.Data
  public static class Data {
    private PersonDTO hentPerson;
  }
}

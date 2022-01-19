package no.nav.bidrag.aktoerregister.service.graphql;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GraphQLQuery {
  private String query;
  private Object variables;
}

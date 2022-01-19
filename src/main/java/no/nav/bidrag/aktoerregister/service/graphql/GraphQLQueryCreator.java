package no.nav.bidrag.aktoerregister.service.graphql;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import lombok.val;
import org.springframework.core.io.ClassPathResource;

public class GraphQLQueryCreator {

  public static final String HENT_PERSON_QUERY = "graphql/hent-person-adresse.graphql";

  public static GraphQLQuery create(String file, Object variables) {
    String query = readGraphQLQueryFromFile(file);
    return GraphQLQuery.builder().query(query).variables(variables).build();
  }

  private static String readGraphQLQueryFromFile(String file) {
    val resource = new ClassPathResource(file);
    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(resource.getInputStream(), UTF_8))) {
      return reader.lines().collect(Collectors.joining("\n"));
    } catch (IOException e) {
      return null;
    }
  }
}

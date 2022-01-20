package no.nav.bidrag.aktoerregister.service;

import static no.nav.bidrag.aktoerregister.service.graphql.GraphQLQueryCreator.HENT_PERSON_QUERY;

import java.util.Map;
import no.nav.bidrag.aktoerregister.domene.AktoerDTO;
import no.nav.bidrag.aktoerregister.domene.PersonDTO;
import no.nav.bidrag.aktoerregister.exception.AktoerNotFoundException;
import no.nav.bidrag.aktoerregister.exception.PDLServiceException;
import no.nav.bidrag.aktoerregister.service.graphql.GraphQLQueryCreator;
import no.nav.bidrag.aktoerregister.service.graphql.GraphQLResponse;
import no.nav.bidrag.commons.web.HttpHeaderRestTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import no.nav.bidrag.aktoerregister.service.graphql.GraphQLQuery;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class PDLServiceImpl implements PDLService {

  private final HttpHeaderRestTemplate pdlRestTemplate;

  public PDLServiceImpl(@Qualifier("pdl") HttpHeaderRestTemplate pdlRestTemplate) {
    this.pdlRestTemplate = pdlRestTemplate;
  }

  @Override
  public AktoerDTO hentAktoer(String id) {
    return null;
  }

  @Override
  public PersonDTO hentRawAktoer(String id) throws PDLServiceException, AktoerNotFoundException {
    GraphQLQuery graphQLQuery = GraphQLQueryCreator.create(HENT_PERSON_QUERY, Map.of("ident", id));
    GraphQLResponse graphQLResponse;
    try {
      graphQLResponse = pdlRestTemplate.postForEntity("/graphql", graphQLQuery, GraphQLResponse.class).getBody();
    } catch (HttpClientErrorException e) {
      throw new PDLServiceException("Feil ved kall mot PDL", e);
    }
    catch (Exception e) {
      throw new PDLServiceException(e.getMessage(), e);
    }
    return validateResponse(graphQLResponse);
  }

  private PersonDTO validateResponse(GraphQLResponse graphQLResponse) throws PDLServiceException, AktoerNotFoundException {
    if (graphQLResponse == null) {
      throw new PDLServiceException("Response fra PDL er null");
    }

    if (graphQLResponse.getErrors() != null) {
      throw new PDLServiceException(graphQLResponse.getErrors());
    }
    return graphQLResponse.getData().getHentPerson();
  }
}

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import no.nav.bidrag.aktoerregister.service.graphql.GraphQLQuery;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class PDLServiceImpl implements PDLService {

  private final HttpHeaderRestTemplate pdlRestTemplate;

  private static final Logger logger = LoggerFactory.getLogger(PDLService.class);


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
    logger.info("Query: "  + graphQLQuery.getQuery());
    GraphQLResponse graphQLResponse = null;
    try {
      graphQLResponse = pdlRestTemplate.postForEntity("/", graphQLQuery, GraphQLResponse.class).getBody();
      logger.info("Response: " + graphQLResponse);
    } catch (HttpClientErrorException e) {
      throw new PDLServiceException("Feil ved kall mot PDL", e);
    }
    return validateResponse(graphQLResponse);
  }

  private PersonDTO validateResponse(GraphQLResponse graphQLResponse) throws PDLServiceException, AktoerNotFoundException {
    logger.info("Validating response: " + graphQLResponse);
    if (graphQLResponse == null) {
      throw new PDLServiceException("Response fra PDL er null");
    }

    if (!graphQLResponse.getErrors().isEmpty()) {
      throw new PDLServiceException(graphQLResponse.getErrors());
    }
    logger.info("Returning data: " + graphQLResponse.getData().getHentPerson());
    return graphQLResponse.getData().getHentPerson();
  }
}

package no.nav.bidrag.aktoerregister.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nimbusds.jose.JOSEObjectType;
import java.util.Collections;
import no.nav.bidrag.aktoerregister.AktoerregisterApplication;
import no.nav.bidrag.aktoerregister.domene.AktoerDTO;
import no.nav.bidrag.aktoerregister.domene.HendelseDTO;
import no.nav.bidrag.aktoerregister.service.AktoerregisterService;
import no.nav.security.mock.oauth2.MockOAuth2Server;
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback;
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(classes = AktoerregisterApplication.class)
@AutoConfigureMockMvc
@Testcontainers
@AutoConfigureTestDatabase(replace = Replace.NONE)
@EnableMockOAuth2Server
public class AktoerregisterControllerTest {

  @Container
  static PostgreSQLContainer<?> database =
      new PostgreSQLContainer<>("postgres")
          .withDatabaseName("test_db")
          .withUsername("root")
          .withPassword("root")
          .withInitScript("db-setup.sql");

  @DynamicPropertySource
  static void setDatasourceProperties(DynamicPropertyRegistry propertyRegistry) {
    propertyRegistry.add("spring.datasource.url", database::getJdbcUrl);
  }

  @Autowired public MockOAuth2Server auth2Server;

  @Value("${aktoerregister.scope}")
  private String aktoerregisterScope;

  @Autowired private MockMvc mockMvc;

  @MockBean private AktoerregisterService aktoerregisterService;

  private final String hentAktoerUrl = "/aktoer";

  private final String hentHendelserUrl = "/hendelser";

  private final String aktoerId = "{\"aktoerId\": \"1234\", \"identtype\": \"AKTOERNUMMER\"}";

  private final String issuer = "maskinporten";

  private final String clientId = "someClientId";

  @Test
  public void TestHentAktoerWithValidToken() throws Exception {
    when(aktoerregisterService.hentAktoer(any())).thenReturn(new AktoerDTO());
    String token = token(issuer, clientId, aktoerregisterScope);
    mockMvc
        .perform(
            post(hentAktoerUrl)
                .header(AUTHORIZATION, token)
                .content(aktoerId)
                .contentType(APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  public void TestHentAktoerWithInvalidToken() throws Exception {
    String token = token(issuer, clientId, "some-random-scope");
    mockMvc
        .perform(
            post(hentAktoerUrl)
                .header(AUTHORIZATION, token)
                .content(aktoerId)
                .contentType(APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
    token = token("some-random-issuer", clientId, aktoerregisterScope);
    mockMvc
        .perform(
            post(hentAktoerUrl)
                .header(AUTHORIZATION, token)
                .content(aktoerId)
                .contentType(APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void TestHentHendelserWithValidToken() throws Exception {
    when(aktoerregisterService.hentHendelser(anyInt(), anyInt()))
        .thenReturn(Collections.singletonList(new HendelseDTO()));
    String token = token(issuer, clientId, aktoerregisterScope);
    mockMvc
        .perform(
            get(hentHendelserUrl)
                .param("fraSekvensnummer", "1")
                .param("antall", "100")
                .header(AUTHORIZATION, token))
        .andExpect(status().isOk());
  }

  @Test
  public void TestHentHendelserWithInvalidToken() throws Exception {
    String token = token(issuer, clientId, "some-random-scope");
    mockMvc
        .perform(
            get(hentHendelserUrl)
                .param("fraSekvensnummer", "1")
                .param("antall", "100")
                .header(AUTHORIZATION, token))
        .andExpect(status().isUnauthorized());
    token = token("some-random-issuer", clientId, aktoerregisterScope);
    mockMvc
        .perform(
            get(hentHendelserUrl)
                .param("fraSekvensnummer", "1")
                .param("antall", "100")
                .header(AUTHORIZATION, token))
        .andExpect(status().isUnauthorized());
  }

  private String token(String issuerId, String subject, String scope) {
    return "Bearer "
        + auth2Server
            .issueToken(
                issuerId,
                subject,
                new DefaultOAuth2TokenCallback(
                    issuerId,
                    subject,
                    JOSEObjectType.JWT.getType(),
                    Collections.emptyList(),
                    Collections.singletonMap("scope", scope),
                    3600))
            .serialize();
  }
}

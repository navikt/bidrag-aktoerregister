package no.nav.bidrag.aktoerregister.util;

import no.nav.security.mock.oauth2.MockOAuth2Server;
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@AutoConfigureTestDatabase(replace = Replace.NONE)
@EnableMockOAuth2Server
public class TestContainerTest {

  static final PostgreSQLContainer database;

  static { database = (PostgreSQLContainer) new PostgreSQLContainer("postgres")
      .withDatabaseName("test_db")
      .withUsername("root")
      .withPassword("root")
      .withReuse(true);
    database.start();
  }

  @DynamicPropertySource
  static void setDatasourceProperties(DynamicPropertyRegistry propertyRegistry) {
    propertyRegistry.add("spring.datasource.url", database::getJdbcUrl);
  }

  @Autowired
  public MockOAuth2Server auth2Server;
}

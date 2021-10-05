package no.nav.bidrag.aktoerregister.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import no.nav.bidrag.aktoerregister.AktoerregisterApplicationTest;
import no.nav.bidrag.aktoerregister.domene.IdenttypeDTO;
import no.nav.bidrag.aktoerregister.persistence.entities.Adresse;
import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;
import no.nav.bidrag.aktoerregister.persistence.entities.Hendelse;
import no.nav.bidrag.aktoerregister.persistence.entities.Kontonummer;
import no.nav.bidrag.aktoerregister.persistence.repository.AktoerJpaRepository;
import no.nav.bidrag.aktoerregister.persistence.repository.AktoerRepository;
import no.nav.bidrag.aktoerregister.persistence.repository.HendelseJpaRepository;
import no.nav.bidrag.aktoerregister.persistence.repository.HendelseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(classes = AktoerregisterApplicationTest.class)
@Testcontainers
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class AktoerRepositoryTests {

  @Container
  static PostgreSQLContainer database = new PostgreSQLContainer("postgres").withDatabaseName("test_db").withUsername("root").withPassword("root");

  @DynamicPropertySource
  static void setDatasourceProperties(DynamicPropertyRegistry propertyRegistry) {
    propertyRegistry.add("spring.datasource.url", database::getJdbcUrl);
  }

  @Autowired
  private AktoerRepository aktoerRepository;

  @Autowired
  private AktoerJpaRepository aktoerJpaRepository;

  @Autowired
  private HendelseRepository hendelseRepository;

  @Autowired
  private HendelseJpaRepository hendelseJpaRepository;

  @Test
  public void injectedRepositoriesIsNotNull() {
    assertNotNull(aktoerRepository);
    assertNotNull(aktoerJpaRepository);
    assertNotNull(hendelseRepository);
    assertNotNull(hendelseJpaRepository);
  }

  @Test
  public void TestInsertOrUpdate() {
    List<Aktoer> aktoerer = generateAktoerList(20);
    for (Aktoer aktoer : aktoerer) {
      aktoerRepository.insertOrUpdateAktoer(aktoer);
    }

    List<Aktoer> savedAktoerer = aktoerJpaRepository.findAll();
    List<Hendelse> savedHendelser = hendelseJpaRepository.findAll();

    assertEquals(20, savedAktoerer.size());
    assertEquals(20, savedHendelser.size());

    // Updating the same aktoerer to test that new hendelser are created
    for (Aktoer aktoer : aktoerer) {
      aktoerRepository.insertOrUpdateAktoer(aktoer);
    }

    savedAktoerer = aktoerJpaRepository.findAll();
    savedHendelser = hendelseJpaRepository.findAll();

    assertEquals(20, savedAktoerer.size());
    assertEquals(40, savedHendelser.size());
  }

  private List<Aktoer> generateAktoerList(int numberOfAktoers) {
    List<Aktoer> aktoerList = new ArrayList<>();
    for (int i = 0; i < numberOfAktoers; i++) {
      Aktoer aktoer = new Aktoer();
      aktoer.setAktoerId(UUID.randomUUID().toString());
      aktoer.setAktoerType(IdenttypeDTO.PERSONNUMMER.name());

      addAdresse(aktoer, i);
      addKontonummer(aktoer, i);
      aktoerList.add(aktoer);
    }
    return aktoerList;
  }

  private void addAdresse(Aktoer aktoer, int aktoerNr) {
    Adresse adresse = new Adresse();
    adresse.setLand("Norge");
    adresse.setPostnr("0682");
    adresse.setPoststed("Oslo");
    adresse.setAdresselinje1("Testgate " + aktoerNr);
    aktoer.setAdresse(adresse);
  }

  private void addKontonummer(Aktoer aktoer, int aktoerNr) {
    Kontonummer kontonummer = new Kontonummer();
    kontonummer.setNorskKontonr(String.valueOf(aktoerNr));
    aktoer.setKontonummer(kontonummer);
  }
}

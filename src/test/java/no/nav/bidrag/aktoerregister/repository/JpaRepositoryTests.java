package no.nav.bidrag.aktoerregister.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import no.nav.bidrag.aktoerregister.AktoerregisterApplicationTest;
import no.nav.bidrag.aktoerregister.domene.IdenttypeDTO;
import no.nav.bidrag.aktoerregister.persistence.entities.Adresse;
import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;
import no.nav.bidrag.aktoerregister.persistence.entities.Hendelse;
import no.nav.bidrag.aktoerregister.persistence.entities.Kontonummer;
import no.nav.bidrag.aktoerregister.persistence.repository.AdresseJpaRepository;
import no.nav.bidrag.aktoerregister.persistence.repository.AktoerJpaRepository;
import no.nav.bidrag.aktoerregister.persistence.repository.HendelseJpaRepository;
import no.nav.bidrag.aktoerregister.persistence.repository.KontonummerJpaRepository;
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(classes = AktoerregisterApplicationTest.class)
@Testcontainers
@AutoConfigureTestDatabase(replace = Replace.NONE)
@EnableMockOAuth2Server
public class JpaRepositoryTests {

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

  @Autowired private HendelseJpaRepository hendelseJpaRepository;

  @Autowired private AktoerJpaRepository aktoerJpaRepository;

  @Autowired private AdresseJpaRepository adresseJpaRepository;

  @Autowired private KontonummerJpaRepository kontonummerJpaRepository;

  @Test
  public void injectedRepositoriesIsNotNull() {
    assertNotNull(hendelseJpaRepository);
    assertNotNull(aktoerJpaRepository);
    assertNotNull(adresseJpaRepository);
    assertNotNull(kontonummerJpaRepository);
  }

  @BeforeEach
  public void Setup() {
    aktoerJpaRepository.deleteAll();
  }

  @Test
  public void validateCascadeBetweenAktoerAndSubTables() {
    List<Aktoer> aktoerer = generateAktoerList(20, 2);
    aktoerJpaRepository.saveAll(aktoerer);

    List<Adresse> adresser = adresseJpaRepository.findAll();
    List<Kontonummer> kontonummer = kontonummerJpaRepository.findAll();
    List<Hendelse> hendelser = hendelseJpaRepository.findAll();
    List<Aktoer> savedAktoerer = aktoerJpaRepository.findAll();

    assertEquals(20, adresser.size());
    assertEquals(20, kontonummer.size());
    assertEquals(40, hendelser.size());
    assertEquals(20, savedAktoerer.size());

    aktoerJpaRepository.delete(savedAktoerer.get(0));

    adresser = adresseJpaRepository.findAll();
    kontonummer = kontonummerJpaRepository.findAll();
    hendelser = hendelseJpaRepository.findAll();
    savedAktoerer = aktoerJpaRepository.findAll();

    assertEquals(19, adresser.size());
    assertEquals(19, kontonummer.size());
    assertEquals(38, hendelser.size());
    assertEquals(19, savedAktoerer.size());
  }

  @Test
  public void validateHendelsePagination() {
    assertEquals(0, aktoerJpaRepository.count());
    List<Aktoer> aktoerer = generateAktoerList(20, 3);
    aktoerJpaRepository.saveAll(aktoerer);

    List<Hendelse> latestHendelser =
        hendelseJpaRepository.getHendelserWithUniqueAktoerPageable(0, Pageable.ofSize(5)).stream()
            .sorted(Comparator.comparing(Hendelse::getSekvensnummer))
            .collect(Collectors.toList());

    // Making sure each hendelse has a unique aktoerId.
    List<String> uniqueAktoerIds =
        latestHendelser.stream()
            .map(hendelse -> hendelse.getAktoer().getAktoerId())
            .distinct()
            .toList();

    assertEquals(5, latestHendelser.size());
    assertEquals(5, uniqueAktoerIds.size());
    assertTrue(latestHendelser.get(0).getSekvensnummer() > 0);

    int lastReceivedSekvensnummer =
        latestHendelser.get(latestHendelser.size() - 1).getSekvensnummer();

    latestHendelser =
        hendelseJpaRepository
            .getHendelserWithUniqueAktoerPageable(
                lastReceivedSekvensnummer + 1, Pageable.ofSize(10))
            .stream()
            .sorted(Comparator.comparing(Hendelse::getSekvensnummer))
            .collect(Collectors.toList());

    uniqueAktoerIds =
        latestHendelser.stream()
            .map(hendelse -> hendelse.getAktoer().getAktoerId())
            .distinct()
            .toList();

    assertEquals(10, latestHendelser.size());
    assertEquals(10, uniqueAktoerIds.size());
    assertTrue(latestHendelser.get(0).getSekvensnummer() >= lastReceivedSekvensnummer);

    lastReceivedSekvensnummer = latestHendelser.get(latestHendelser.size() - 1).getSekvensnummer();

    latestHendelser =
        hendelseJpaRepository
            .getHendelserWithUniqueAktoerPageable(
                lastReceivedSekvensnummer + 1, Pageable.ofSize(20))
            .stream()
            .sorted(Comparator.comparing(Hendelse::getSekvensnummer))
            .collect(Collectors.toList());

    uniqueAktoerIds =
        latestHendelser.stream()
            .map(hendelse -> hendelse.getAktoer().getAktoerId())
            .distinct()
            .toList();

    assertEquals(5, latestHendelser.size());
    assertEquals(5, uniqueAktoerIds.size());
    assertTrue(latestHendelser.get(0).getSekvensnummer() > lastReceivedSekvensnummer);
  }

  private List<Aktoer> generateAktoerList(int numberOfAktoers, int numberOfHendelser) {
    List<Aktoer> aktoerList = new ArrayList<>();
    for (int i = 0; i < numberOfAktoers; i++) {
      Aktoer aktoer = new Aktoer();
      aktoer.setAktoerId(UUID.randomUUID().toString());
      aktoer.setAktoerType(IdenttypeDTO.PERSONNUMMER.name());

      addHendelser(numberOfHendelser, aktoer);
      addAdresse(aktoer, i);
      addKontonummer(aktoer, i);
      aktoerList.add(aktoer);
    }
    return aktoerList;
  }

  private void addHendelser(int numberOfHendelser, Aktoer aktoer) {
    for (int j = 0; j < numberOfHendelser; j++) {
      Hendelse hendelse = new Hendelse();
      hendelse.setAktoer(aktoer);
      aktoer.getHendelser().add(hendelse);
    }
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

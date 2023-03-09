package no.nav.bidrag.aktoerregister.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import no.nav.bidrag.aktoerregister.AktoerregisterApplication;
import no.nav.bidrag.aktoerregister.dto.enumer.Identtype;
import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;
import no.nav.bidrag.aktoerregister.persistence.entities.Hendelse;
import no.nav.bidrag.aktoerregister.persistence.repository.AktoerJpaRepository;
import no.nav.bidrag.aktoerregister.persistence.repository.AktoerRepository;
import no.nav.bidrag.aktoerregister.persistence.repository.HendelseJpaRepository;
import no.nav.bidrag.aktoerregister.persistence.repository.HendelseRepository;
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

@SpringBootTest(classes = AktoerregisterApplication.class)
@Testcontainers
@AutoConfigureTestDatabase(replace = Replace.NONE)
@EnableMockOAuth2Server
public class AktoerRepositoryTests {

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

  @Autowired private AktoerRepository aktoerRepository;

  @Autowired private AktoerJpaRepository aktoerJpaRepository;

  @Autowired private HendelseRepository hendelseRepository;

  @Autowired private HendelseJpaRepository hendelseJpaRepository;

  @BeforeEach
  public void Setup() {
    aktoerJpaRepository.deleteAll();
  }

  @Test
  public void skalTesteOpprettEllerOppdatertAktoerer() {
    List<Aktoer> aktoerer = opprettAktoerListe(20);
    for (Aktoer aktoer : aktoerer) {
      aktoerRepository.opprettEllerOppdaterAktoer(aktoer);
    }

    List<Aktoer> savedAktoerer = aktoerJpaRepository.findAll();
    List<Hendelse> savedHendelser = hendelseJpaRepository.findAll();

    assertEquals(20, savedAktoerer.size());
    assertEquals(20, savedHendelser.size());

    // Updating the same aktoerer to test that new hendelser are created
    for (Aktoer aktoer : aktoerer) {
      aktoerRepository.opprettEllerOppdaterAktoer(aktoer);
    }

    savedAktoerer = aktoerJpaRepository.findAll();
    savedHendelser = hendelseJpaRepository.findAll();

    assertEquals(20, savedAktoerer.size());
    assertEquals(40, savedHendelser.size());
  }

  @Test
  public void skalOppretteEllerOppdatereAktoerMedListe() {
    List<Aktoer> aktoerer = opprettAktoerListe(20);
    aktoerRepository.opprettEllerOppdaterAktoerer(aktoerer);
    hendelseRepository.opprettHendelser(aktoerer);

    List<Aktoer> lagredeAktoerer =
        aktoerJpaRepository
            .findAllByAktoerType(Identtype.PERSONNUMMER.name(), Pageable.ofSize(100))
            .stream()
            .toList();
    List<Hendelse> lagredeHendelser = hendelseJpaRepository.findAll();

    assertEquals(20, lagredeAktoerer.size());
    assertEquals(20, lagredeHendelser.size());

    List<Aktoer> sublist = lagredeAktoerer.subList(0, 10);

    aktoerRepository.opprettEllerOppdaterAktoerer(sublist);
    hendelseRepository.opprettHendelser(sublist);

    lagredeAktoerer =
        aktoerJpaRepository
            .findAllByAktoerType(Identtype.PERSONNUMMER.name(), Pageable.ofSize(100))
            .stream()
            .toList();
    lagredeHendelser = hendelseJpaRepository.findAll();
    assertEquals(20, lagredeAktoerer.size());
    assertEquals(30, lagredeHendelser.size());

    List<String> aktoerIds = sublist.stream().map(Aktoer::getAktoerIdent).toList();

    List<Hendelse> aktoerHendelser =
        lagredeHendelser.stream()
            .filter(hendelse -> aktoerIds.contains(hendelse.getAktoer().getAktoerIdent()))
            .toList();

    Map<String, List<Hendelse>> hendelseMap = new HashMap<>();
    for (Hendelse hendelse : aktoerHendelser) {
      if (!hendelseMap.containsKey(hendelse.getAktoer().getAktoerIdent())) {
        hendelseMap.put(hendelse.getAktoer().getAktoerIdent(), new ArrayList<>());
      }
      List<Hendelse> hendelser = hendelseMap.get(hendelse.getAktoer().getAktoerIdent());
      hendelser.add(hendelse);
      hendelseMap.put(hendelse.getAktoer().getAktoerIdent(), hendelser);
    }

    sublist.forEach(
        aktoer -> assertEquals(2, hendelseMap.get(aktoer.getAktoerIdent()).size()));
  }

  private List<Aktoer> opprettAktoerListe(int numberOfAktoers) {
    List<Aktoer> aktoerList = new ArrayList<>();
    for (int i = 0; i < numberOfAktoers; i++) {
      Aktoer aktoer = new Aktoer();
      aktoer.setAktoerIdent(UUID.randomUUID().toString());
      aktoer.setAktoerType(Identtype.PERSONNUMMER.name());

      leggTilAdresse(aktoer, i);
      leggTilKontonummer(aktoer, i);
      aktoerList.add(aktoer);
    }
    return aktoerList;
  }

  private void leggTilAdresse(Aktoer aktoer, int aktoerNr) {
    aktoer.setLand("Norge");
    aktoer.setPostnr("0682");
    aktoer.setPoststed("Oslo");
    aktoer.setAdresselinje1("Testgate " + aktoerNr);
  }

  private void leggTilKontonummer(Aktoer aktoer, int aktoerNr) {
    aktoer.setNorskKontonr(String.valueOf(aktoerNr));
  }
}

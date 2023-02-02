package no.nav.bidrag.aktoerregister.repository;

import java.util.Comparator;
import java.util.List;
import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;
import no.nav.bidrag.aktoerregister.persistence.entities.Hendelse;
import no.nav.bidrag.aktoerregister.persistence.repository.AktoerRepository;

public record AktoerRepositoryMock(MockDB mockDB) implements AktoerRepository {

  @Override
  public Aktoer insertOrUpdateAktoer(Aktoer aktoer) {
    aktoer.addHendelse(createHendelse(aktoer));
    mockDB.aktoerMap.put(aktoer.getAktoerIdent(), aktoer);
    for (Hendelse hendelse : aktoer.getHendelser()) {
      insertHendelse(hendelse);
    }
    return aktoer;
  }

  @Override
  public List<Aktoer> insertOrUpdateAktoerer(List<Aktoer> aktoerList) {
    for (Aktoer aktoer : aktoerList) {
      insertOrUpdateAktoer(aktoer);
    }
    return aktoerList;
  }

  @Override
  public Aktoer getAktoer(String aktoerIdent) {
    return mockDB.aktoerMap.get(aktoerIdent);
  }

  @Override
  public void deleteAktoer(String aktoerId) {
    mockDB.aktoerMap.remove(aktoerId);
  }

  private void insertHendelse(Hendelse hendelse) {
    int sekvensNummer = getSekvensNummer(hendelse);
    hendelse.setSekvensnummer(sekvensNummer);
    mockDB.hendelseMap.put(sekvensNummer, hendelse);
  }

  private int getSekvensNummer(Hendelse hendelse) {
    return hendelse.getSekvensnummer() != 0
        ? hendelse.getSekvensnummer()
        : mockDB.hendelseMap.keySet().stream().max(Comparator.naturalOrder()).orElse(0) + 1;
  }

  private Hendelse createHendelse(Aktoer aktoer) {
    Hendelse hendelse = new Hendelse();
    hendelse.setAktoer(aktoer);
    return hendelse;
  }
}

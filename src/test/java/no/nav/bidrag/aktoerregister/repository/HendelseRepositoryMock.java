package no.nav.bidrag.aktoerregister.repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import no.nav.bidrag.aktoerregister.persistence.entities.Hendelse;
import no.nav.bidrag.aktoerregister.persistence.repository.HendelseRepository;

public record HendelseRepositoryMock(MockDB mockDB) implements HendelseRepository {

  @Override
  public List<Hendelse> hentHendelser(int fraSekvensnummer, int antallHendelser) {
    Map<String, List<Hendelse>> hendelseHashMap = mockDB.hendelseMap.values().stream()
        .filter(hendelse -> hendelse.getSekvensnummer() > fraSekvensnummer)
        .collect(Collectors.groupingBy(hendelse -> hendelse.getAktoer().getAktoerId()));
    return hendelseHashMap.values().stream().map(hendelses -> hendelses.stream().max(Comparator.comparingInt(Hendelse::getSekvensnummer)).orElse(null)
    ).toList();
  }
}

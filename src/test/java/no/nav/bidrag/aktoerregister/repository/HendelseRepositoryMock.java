package no.nav.bidrag.aktoerregister.repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;
import no.nav.bidrag.aktoerregister.persistence.entities.Hendelse;
import no.nav.bidrag.aktoerregister.persistence.repository.HendelseRepository;

public record HendelseRepositoryMock(MockDB mockDB) implements HendelseRepository {

  @Override
  public List<Hendelse> hentHendelser(int fraSekvensnummer, int antallHendelser) {
    Map<String, List<Hendelse>> hendelseHashMap =
        mockDB.hendelseMap.values().stream()
            .filter(hendelse -> hendelse.getSekvensnummer() > fraSekvensnummer)
            .collect(Collectors.groupingBy(hendelse -> hendelse.getAktoer().getAktoerIdent()));
    return hendelseHashMap.values().stream()
        .map(
            hendelses ->
                hendelses.stream()
                    .max(Comparator.comparingInt(Hendelse::getSekvensnummer))
                    .orElse(null))
        .toList();
  }

  @Override
  public void insertHendelser(List<Aktoer> updatedAktoerer) {
    List<Hendelse> hendelser = updatedAktoerer.stream().map(Hendelse::new).toList();
    int max = mockDB.hendelseMap.keySet().stream().max(Integer::compareTo).orElse(0);
    for (Hendelse hendelse : hendelser) {
      max++;
      hendelse.setSekvensnummer(max);
      mockDB.hendelseMap.put(max, hendelse);
    }
  }
}

package no.nav.bidrag.aktoerregister.persistence.repository;

import java.util.List;
import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;
import no.nav.bidrag.aktoerregister.persistence.entities.Hendelse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public class HendelseRepositoryImpl implements HendelseRepository {

  private final HendelseJpaRepository hendelseJpaRepository;

  @Autowired
  public HendelseRepositoryImpl(HendelseJpaRepository hendelseJpaRepository) {
    this.hendelseJpaRepository = hendelseJpaRepository;
  }

  @Override
  public List<Hendelse> hentHendelser(int fraSekvensnummer, int antallHendelser) {
    return hendelseJpaRepository.getHendelserWithUniqueAktoerPageable(
        fraSekvensnummer, Pageable.ofSize(antallHendelser));
  }

  @Override
  public void insertHendelser(List<Aktoer> updatedAktoerer) {
    List<Hendelse> hendelser = updatedAktoerer.stream().map(Hendelse::new).toList();
    hendelseJpaRepository.saveAll(hendelser);
  }
}

package no.nav.bidrag.aktoerregister.persistence.repository;

import java.util.List;
import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;
import no.nav.bidrag.aktoerregister.persistence.entities.Hendelse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public class AktoerRepositoryImpl implements AktoerRepository {

  private final AktoerJpaRepository aktoerJpaRepository;

  @Autowired
  public AktoerRepositoryImpl(AktoerJpaRepository aktoerJpaRepository) {
    this.aktoerJpaRepository = aktoerJpaRepository;
  }

  @Override
  public Aktoer insertOrUpdateAktoer(Aktoer aktoer) {
    aktoer.addHendelse(new Hendelse(aktoer));
    return aktoerJpaRepository.save(aktoer);
  }

  @Override
  public List<Aktoer> insertOrUpdateAktoerer(List<Aktoer> aktoerList) {
    return aktoerJpaRepository.saveAll(aktoerList);
  }

  @Override
  public Aktoer getAktoer(String aktoerIdent) {
    return aktoerJpaRepository.findByAktoerIdent(aktoerIdent);
  }

  @Override
  public void deleteAktoer(String aktoerIdent) {
    aktoerJpaRepository.deleteAktoerByAktoerIdent(aktoerIdent);
  }
}

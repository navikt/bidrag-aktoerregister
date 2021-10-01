package no.nav.bidrag.aktoerregister.persistence.repository;

import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;
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
    return aktoerJpaRepository.save(aktoer);
  }

  @Override
  public Aktoer getAktoer(String aktoerId) {
    return aktoerJpaRepository.findById(aktoerId).orElse(null);
  }

  @Override
  public void deleteAktoer(String aktoerId) {
    aktoerJpaRepository.deleteById(aktoerId);
  }
}

package no.nav.bidrag.aktoerregister.persistence.repository;

import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AktoerJpaRepository extends JpaRepository<Aktoer, String> {

}

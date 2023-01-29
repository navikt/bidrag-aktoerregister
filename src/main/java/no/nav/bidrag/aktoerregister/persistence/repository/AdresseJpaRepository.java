package no.nav.bidrag.aktoerregister.persistence.repository;

import no.nav.bidrag.aktoerregister.persistence.entities.Adresse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdresseJpaRepository extends JpaRepository<Adresse, Integer> {}

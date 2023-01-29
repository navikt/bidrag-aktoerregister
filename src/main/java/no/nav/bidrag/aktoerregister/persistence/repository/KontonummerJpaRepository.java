package no.nav.bidrag.aktoerregister.persistence.repository;

import no.nav.bidrag.aktoerregister.persistence.entities.Kontonummer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KontonummerJpaRepository extends JpaRepository<Kontonummer, Integer> {}

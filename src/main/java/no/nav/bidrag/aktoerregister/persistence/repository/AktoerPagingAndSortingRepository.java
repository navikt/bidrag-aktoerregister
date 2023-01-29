package no.nav.bidrag.aktoerregister.persistence.repository;

import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AktoerPagingAndSortingRepository
    extends PagingAndSortingRepository<Aktoer, String> {}

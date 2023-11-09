package no.nav.bidrag.aktoerregister.batch.person

import no.nav.bidrag.aktoerregister.dto.enumer.Identtype
import no.nav.bidrag.aktoerregister.persistence.entities.Aktør
import no.nav.bidrag.aktoerregister.persistence.repository.AktørRepository
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.data.RepositoryItemReader
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.Collections

@Component
class PersonBatchReader(aktoerRepository: AktørRepository) : RepositoryItemReader<Aktør>(), ItemReader<Aktør> {
    init {
        this.setRepository(aktoerRepository)
        this.setMethodName("finnAlleIkkeKjørte")
        this.setArguments(listOf(Identtype.PERSONNUMMER.name, Timestamp.valueOf(LocalDateTime.of(2023, 11, 8, 13, 20, 0))))
        this.setPageSize(100)
        this.setSort(Collections.singletonMap("aktoer.aktoer_ident", Sort.Direction.ASC))
    }
}

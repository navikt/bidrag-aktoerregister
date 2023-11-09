package no.nav.bidrag.aktoerregister.persistence.repository

import no.nav.bidrag.aktoerregister.persistence.entities.Aktør
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface AktørRepository : JpaRepository<Aktør, String> {

    // Brukes av batchReader
    @Suppress("unused")
    @Query(
        nativeQuery = true,
        value = "SELECT * FROM aktoerregister.aktoer WHERE aktoertype = ?1 AND sist_endret < ?2"
    )
    fun findAllByAktørType(aktørType: String, sistEndret: String, pageable: Pageable): Page<Aktør>

    fun findByAktørIdent(aktørIdent: String): Aktør?

    fun deleteAktørByAktørIdent(aktørIdent: String)
}

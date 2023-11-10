package no.nav.bidrag.aktoerregister.persistence.repository

import no.nav.bidrag.aktoerregister.persistence.entities.Aktør
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface AktørRepository : JpaRepository<Aktør, String> {

    // Brukes av batchReader
    @Suppress("unused")
    fun findAllByAktørType(aktørType: String, pageable: Pageable): Page<Aktør>

    fun findByAktørIdent(aktørIdent: String): Aktør?

    fun deleteAktørByAktørIdent(aktørIdent: String)
}

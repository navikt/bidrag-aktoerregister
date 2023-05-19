package no.nav.bidrag.aktoerregister.persistence.repository

import no.nav.bidrag.aktoerregister.persistence.entities.Aktør
import org.springframework.data.jpa.repository.JpaRepository

interface AktørRepository : JpaRepository<Aktør, String> {

    fun findByAktørIdent(aktørIdent: String): Aktør?

    fun deleteAktørByAktørIdent(aktørIdent: String)
}

package no.nav.bidrag.aktoerregister.persistence.repository

import no.nav.bidrag.aktoerregister.persistence.entities.Aktør

interface AktørRepository {
    fun opprettEllerOppdaterAktør(aktør: Aktør): Aktør
    fun opprettEllerOppdaterAktører(aktørListe: List<Aktør>): List<Aktør>
    fun getAktør(aktørIdent: String): Aktør?
    fun deleteAktør(aktørIdent: String)
}
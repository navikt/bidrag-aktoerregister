package no.nav.bidrag.aktoerregister.persistence.repository

import no.nav.bidrag.aktoerregister.persistence.entities.Aktør
import no.nav.bidrag.aktoerregister.persistence.entities.Hendelse

interface HendelseRepository {
    fun hentHendelser(fraSekvensnummer: Int, antallHendelser: Int): List<Hendelse>
    fun opprettHendelser(updatedAktoerer: List<Aktør>)
}

package no.nav.bidrag.aktoerregister.service

import no.nav.bidrag.aktoerregister.persistence.entities.Aktør

class TSSServiceMock : AktoerService { //TODO() Slette?
    override fun hentAktoer(aktoerIdent: String): Aktør {
        return Aktør(
            aktørIdent = aktoerIdent,
            aktørType = "AKTOERNUMMER",
            adresselinje1 = "Testgate 1"
            )
    }
}
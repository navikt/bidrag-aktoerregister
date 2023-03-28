package no.nav.bidrag.aktoerregister.persistence.repository

import no.nav.bidrag.aktoerregister.persistence.entities.Aktør
import no.nav.bidrag.aktoerregister.persistence.entities.Hendelse
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository

@Repository
@Primary
class AktørRepository(private val aktørJpaRepository: AktørJpaRepository) {

    fun opprettEllerOppdaterAktør(aktør: Aktør): Aktør {
        aktør.addHendelse(Hendelse(aktørIdent = aktør.aktørIdent, aktør = aktør))
        return aktørJpaRepository.save(aktør)
    }

    fun opprettEllerOppdaterAktører(aktørListe: List<Aktør>): List<Aktør> {
        return aktørJpaRepository.saveAll(aktørListe)
    }

    fun getAktør(aktørIdent: String): Aktør? {
        return aktørJpaRepository.findByAktørIdent(aktørIdent)
    }

    fun deleteAktør(aktørIdent: String) {
        aktørJpaRepository.deleteAktørByAktørIdent(aktørIdent)
    }
}
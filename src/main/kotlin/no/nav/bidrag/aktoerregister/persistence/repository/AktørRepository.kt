package no.nav.bidrag.aktoerregister.persistence.repository

import no.nav.bidrag.aktoerregister.persistence.entities.Aktør
import no.nav.bidrag.aktoerregister.persistence.entities.Hendelse
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository

@Repository
@Primary
class AktørRepository(
    private val aktørJpaRepository: AktørJpaRepository
) {

    fun opprettEllerOppdaterAktør(aktør: Aktør): Aktør {
        val nyAktør = aktørJpaRepository.save(aktør)
        aktør.tidligereIdenter.forEach {
            it.aktør = aktør
        }
        aktør.dødsbo?.aktør = aktør
        aktør.addHendelse(Hendelse(aktørIdent = aktør.aktørIdent, aktør = aktør))
        return nyAktør
    }

    fun opprettEllerOppdaterAktører(aktørListe: List<Aktør>): List<Aktør> {
        val nyAktørListe = aktørJpaRepository.saveAll(aktørListe)
        aktørListe.forEach { aktør ->
            aktør.tidligereIdenter.forEach { it.aktør = aktør }
            aktør.dødsbo?.aktør = aktør
        }
        return nyAktørListe
    }

    fun getAktør(aktørIdent: String): Aktør? {
        return aktørJpaRepository.findByAktørIdent(aktørIdent)
    }

    fun deleteAktør(aktørIdent: String) {
        aktørJpaRepository.deleteAktørByAktørIdent(aktørIdent)
    }
}

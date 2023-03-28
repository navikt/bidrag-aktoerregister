package no.nav.bidrag.aktoerregister.persistence.repository

import no.nav.bidrag.aktoerregister.persistence.entities.Aktør
import no.nav.bidrag.aktoerregister.persistence.entities.Hendelse
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository

@Repository
@Primary
class AktørRepositoryImpl(private val aktørJpaRepository: AktørJpaRepository) : AktørRepository {

    override fun opprettEllerOppdaterAktør(aktør: Aktør): Aktør {
        aktør.addHendelse(Hendelse(aktørIdent = aktør.aktørIdent, aktør = aktør))
        return aktørJpaRepository.save(aktør)
    }

    override fun opprettEllerOppdaterAktører(aktørListe: List<Aktør>): List<Aktør> {
        return aktørJpaRepository.saveAll(aktørListe)
    }

    override fun getAktør(aktørIdent: String): Aktør? {
        return aktørJpaRepository.findByAktørIdent(aktørIdent)
    }

    override fun deleteAktør(aktørIdent: String) {
        aktørJpaRepository.deleteAktørByAktørIdent(aktørIdent)
    }
}
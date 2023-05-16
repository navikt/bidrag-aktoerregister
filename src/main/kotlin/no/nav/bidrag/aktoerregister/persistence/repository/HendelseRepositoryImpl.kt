package no.nav.bidrag.aktoerregister.persistence.repository

import no.nav.bidrag.aktoerregister.persistence.entities.Aktør
import no.nav.bidrag.aktoerregister.persistence.entities.Hendelse
import org.springframework.context.annotation.Primary
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
@Primary
class HendelseRepositoryImpl(private val hendelseJpaRepository: HendelseJpaRepository) : HendelseRepository {

    override fun hentHendelser(fraSekvensnummer: Int, antallHendelser: Int): List<Hendelse> {
        val hendelser = hendelseJpaRepository.getAllBySekvensnummerGreaterThan(fraSekvensnummer, Pageable.ofSize(antallHendelser))
        return hendelser.distinctBy { it.aktørIdent }
    }

    override fun opprettHendelser(updatedAktoerer: List<Aktør>) {
        val hendelser: List<Hendelse> = updatedAktoerer.map { Hendelse(aktør = it, aktørIdent = it.aktørIdent) }
        hendelseJpaRepository.saveAll(hendelser)
    }
}

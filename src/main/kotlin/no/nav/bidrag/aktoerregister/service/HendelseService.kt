package no.nav.bidrag.aktoerregister.service

import no.nav.bidrag.aktoerregister.dto.AktoerIdDTO
import no.nav.bidrag.aktoerregister.dto.HendelseDTO
import no.nav.bidrag.aktoerregister.dto.enumer.Identtype
import no.nav.bidrag.aktoerregister.persistence.entities.Aktør
import no.nav.bidrag.aktoerregister.persistence.entities.Hendelse
import no.nav.bidrag.aktoerregister.persistence.repository.HendelseRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class HendelseService(
    private val hendelseRepository: HendelseRepository
) {

    fun hentHendelser(sekvensunummer: Int, antallHendelser: Int): List<HendelseDTO> {
        val hendelser = hendelseRepository.getAllBySekvensnummerGreaterThan(sekvensunummer, Pageable.ofSize(antallHendelser))

        return hendelser.distinctBy { it.aktørIdent }
            .map {
                HendelseDTO(
                    sekvensnummer = it.sekvensnummer,
                    aktoerId = AktoerIdDTO(
                        aktoerId = it.aktørIdent,
                        identtype = Identtype.valueOf(it.aktør.aktørType)
                    )
                )
            }
            .sortedBy { it.sekvensnummer }
    }

    fun opprettHendelserPåAktør(aktør: Aktør, originalIdent: String?) {
        if (originalIdent != null && originalIdent != aktør.aktørIdent) {
            aktør.addHendelse(Hendelse(aktørIdent = originalIdent, aktør = aktør))
        }
        aktør.addHendelse(Hendelse(aktørIdent = aktør.aktørIdent, aktør = aktør))
    }
}

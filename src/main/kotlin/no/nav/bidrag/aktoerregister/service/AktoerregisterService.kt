package no.nav.bidrag.aktoerregister.service

import io.github.oshai.KotlinLogging
import no.nav.bidrag.aktoerregister.consumer.PersonConsumer
import no.nav.bidrag.aktoerregister.consumer.SamhandlerConsumer
import no.nav.bidrag.aktoerregister.dto.aktoerregister.dto.AktoerDTO
import no.nav.bidrag.aktoerregister.dto.aktoerregister.dto.AktoerIdDTO
import no.nav.bidrag.aktoerregister.dto.aktoerregister.dto.HendelseDTO
import no.nav.bidrag.aktoerregister.dto.aktoerregister.enumer.Identtype
import no.nav.bidrag.aktoerregister.exception.AktørNotFoundException
import no.nav.bidrag.aktoerregister.persistence.entities.Aktør
import no.nav.bidrag.aktoerregister.persistence.repository.AktørRepository
import no.nav.bidrag.aktoerregister.persistence.repository.HendelseRepository
import org.springframework.core.convert.ConversionService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val LOGGER = KotlinLogging.logger {}

@Service
class AktoerregisterService(
    private val aktørRepository: AktørRepository,
    private val hendelseRepository: HendelseRepository,
    private val samhandlerConsumer: SamhandlerConsumer,
    private val personConsumer: PersonConsumer,
    private val conversionService: ConversionService
) {

    fun hentAktoer(aktørId: AktoerIdDTO): AktoerDTO {
        val aktørIdent = aktørId.aktoerId
        val aktør = hentAktørFraDatabase(aktørIdent)
            ?: if (aktørId.identtype == Identtype.AKTOERNUMMER) hentAktørFraSamhandler(aktørIdent) else hentAktørFraPerson(aktørIdent)

        return conversionService.convert(aktør, AktoerDTO::class.java) ?: error("Konvertering av aktør til AktoerDTO feilet!")
    }

    fun hentAktørFraSamhandler(aktørIdent: String): Aktør {
        LOGGER.debug("Aktør ikke funnet i databasen. Henter aktør fra bidrag-samhandler")
        val samhandler = samhandlerConsumer.hentSamhandler(aktørIdent) ?: throw AktørNotFoundException("fant ingen aktør med ident: $aktørIdent i bidrag-samhandler")
        conversionService.convert(samhandler, Aktør::class.java)?.let {
            lagreAktoer(it)
            return it
        } ?: error("Konvertering av samhandler til aktør feilet!")
    }

    private fun hentAktørFraPerson(aktørIdent: String): Aktør {
        LOGGER.debug("Aktør ikke funnet i databasen. Henter aktør fra bidrag-person")
        val person = personConsumer.hentPerson(aktørIdent) ?: throw AktørNotFoundException("fant ingen aktør med ident: $aktørIdent i bidrag-person")
        conversionService.convert(person, Aktør::class.java)?.let {
            lagreAktoer(it)
            return it
        } ?: error("Konvertering av person til aktør feilet!")
    }

    fun hentAktørFraDatabase(aktoerIdent: String): Aktør? {
        return aktørRepository.getAktør(aktoerIdent)
    }

    fun hentHendelser(sekvensunummer: Int, antallHendelser: Int): List<HendelseDTO> {
        return hendelseRepository.hentHendelser(sekvensunummer, antallHendelser).map {
            HendelseDTO(
                sekvensnummer = it.sekvensnummer,
                aktoerId = AktoerIdDTO(
                    aktoerId = it.aktør.aktørIdent,
                    identtype = Identtype.valueOf(it.aktør.aktørType)
                )
            )
        }.sortedBy { it.sekvensnummer }
    }

    private fun lagreAktoer(aktør: Aktør): Aktør {
        return aktørRepository.opprettEllerOppdaterAktør(aktør)
    }

    @Transactional
    fun oppdaterAktoer(aktør: Aktør) {
        aktørRepository.opprettEllerOppdaterAktør(aktør)
    }

    @Transactional
    fun oppdaterAktoerer(aktoerliste: List<Aktør>) {
        aktørRepository.opprettEllerOppdaterAktører(aktoerliste)
        hendelseRepository.opprettHendelser(aktoerliste)
    }

    fun slettAktoer(aktoerIdent: String) {
        aktørRepository.deleteAktør(aktoerIdent)
    }
}
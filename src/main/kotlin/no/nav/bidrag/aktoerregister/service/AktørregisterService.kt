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
import no.nav.bidrag.aktoerregister.persistence.repository.TidligereIdenterRepository
import no.nav.bidrag.domain.ident.Ident
import org.springframework.core.convert.ConversionService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val LOGGER = KotlinLogging.logger {}

@Service
class AktørregisterService(
    private val aktørRepository: AktørRepository,
    private val tidligereIdenterRepository: TidligereIdenterRepository,
    private val hendelseRepository: HendelseRepository,
    private val samhandlerConsumer: SamhandlerConsumer,
    private val personConsumer: PersonConsumer,
    private val conversionService: ConversionService
) {

    @Transactional
    fun hentAktoer(aktørId: AktoerIdDTO, tvingOppdatering: Boolean): AktoerDTO {
        val aktørIdent = Ident(aktørId.aktoerId)
        var aktør = hentAktørFraDatabase(aktørIdent)

        if (aktør != null && tvingOppdatering) {
            val hentetAktør = if (aktørId.identtype == Identtype.AKTOERNUMMER) hentAktørFraSamhandler(aktørIdent) else hentAktørFraPerson(aktørIdent)
            if (aktør != hentetAktør) {
                aktør.oppdaterAlleFelter(hentetAktør)
                oppdaterAktør(aktør)
            }
        } else {
            aktør = aktør
                ?: if (aktørId.identtype == Identtype.AKTOERNUMMER) {
                    hentAktørFraSamhandlerOgLagreTilDatabase(aktørIdent)
                } else {
                    hentAktørFraPersonOgLagreTilDatabase(
                        aktørIdent
                    )
                }
        }
        return conversionService.convert(aktør, AktoerDTO::class.java)
            ?: error("Konvertering av aktør til AktoerDTO feilet!")
    }

    fun hentAktørFraSamhandlerOgLagreTilDatabase(aktørIdent: Ident): Aktør {
        LOGGER.debug("Aktør ikke funnet i databasen. Henter aktør fra bidrag-samhandler")
        hentAktørFraSamhandler(aktørIdent).let {
            lagreAktoer(it)
            return it
        }
    }

    fun hentAktørFraSamhandler(aktørIdent: Ident): Aktør {
        val samhandler = samhandlerConsumer.hentSamhandler(aktørIdent)
            ?: throw AktørNotFoundException("Aktør ikke funnet i bidrag-samhandler.")
        return conversionService.convert(samhandler, Aktør::class.java)
            ?: error("Konvertering av samhandler til aktør for ident: $aktørIdent feilet!")
    }

    private fun hentAktørFraPersonOgLagreTilDatabase(personIdent: Ident): Aktør {
        LOGGER.debug("Aktør ikke funnet i databasen. Henter aktør fra bidrag-person")
        hentAktørFraPerson(personIdent).let {
            lagreAktoer(it)
            return it
        }
    }

    fun hentAktørFraPerson(personIdent: Ident): Aktør {
        val person = personConsumer.hentPerson(personIdent)
            ?: throw AktørNotFoundException("Aktør ikke funnet i bidrag-person.")
        return conversionService.convert(person, Aktør::class.java)
            ?: error("Konvertering av person til aktør feilet!")
    }

    fun hentAktørFraDatabase(aktørIdent: Ident): Aktør? {
        return aktørRepository.getAktør(aktørIdent.verdi) ?: tidligereIdenterRepository.findByTidligereAktoerIdent(aktørIdent.verdi)?.aktør
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
    fun oppdaterAktør(aktør: Aktør) {
        aktørRepository.opprettEllerOppdaterAktør(aktør)
    }

    @Transactional
    fun oppdaterAktører(aktørliste: List<Aktør>) {
        aktørRepository.opprettEllerOppdaterAktører(aktørliste)
        hendelseRepository.opprettHendelser(aktørliste)
    }

    fun slettAktoer(aktoerIdent: String) {
        aktørRepository.deleteAktør(aktoerIdent)
    }
}

package no.nav.bidrag.aktoerregister.service

import io.github.oshai.KotlinLogging
import no.nav.bidrag.aktoerregister.consumer.PersonConsumer
import no.nav.bidrag.aktoerregister.consumer.SamhandlerConsumer
import no.nav.bidrag.aktoerregister.dto.AktoerDTO
import no.nav.bidrag.aktoerregister.dto.AktoerIdDTO
import no.nav.bidrag.aktoerregister.dto.enumer.Identtype
import no.nav.bidrag.aktoerregister.exception.AktørNotFoundException
import no.nav.bidrag.aktoerregister.persistence.entities.Aktør
import no.nav.bidrag.aktoerregister.persistence.repository.AktørRepository
import no.nav.bidrag.aktoerregister.persistence.repository.TidligereIdenterRepository
import no.nav.bidrag.domain.ident.Ident
import org.springframework.core.convert.ConversionService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val LOGGER = KotlinLogging.logger {}

@Service
class AktørService(
    private val aktørRepository: AktørRepository,
    private val tidligereIdenterRepository: TidligereIdenterRepository,
    private val hendelseService: HendelseService,
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
                lagreEllerOppdaterAktør(aktør, aktørId.aktoerId)
            }
        } else if (aktør == null) {
            aktør = hentNyAktør(aktørId, aktørIdent)
        }
        return conversionService.convert(aktør, AktoerDTO::class.java)
            ?: error("Konvertering av aktør til AktoerDTO feilet!")
    }

    private fun hentNyAktør(aktørId: AktoerIdDTO, aktørIdent: Ident) =
        if (aktørId.identtype == Identtype.AKTOERNUMMER) {
            hentAktørFraSamhandlerOgLagreTilDatabase(aktørIdent)
        } else {
            hentAktørFraPersonOgLagreTilDatabase(
                aktørIdent
            )
        }

    private fun hentAktørFraSamhandlerOgLagreTilDatabase(aktørIdent: Ident): Aktør {
        LOGGER.debug("Aktør ikke funnet i databasen. Henter aktør fra bidrag-samhandler")
        hentAktørFraSamhandler(aktørIdent).let {
            lagreEllerOppdaterAktør(it, null)
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
            // Om det finnes tidligere identer må vi sjekke om disse eksisterer i databasen fra før av.
            // Om de gjør det skal vi oppdatere og ikke opprette ny ident.
            // Denne situasjonen kan oppstå om en endring av ident blir kalt via REST før vi har tatt imot hendelse fra PDL
            if (it.tidligereIdenter.isNotEmpty()) {
                var aktørFraDatabase: Aktør? = null
                it.tidligereIdenter.forEach {
                    aktørFraDatabase = hentAktørFraDatabase(Ident(it.tidligereAktoerIdent))
                    if (aktørFraDatabase != null) return@forEach
                }
                if (aktørFraDatabase != null) {
                    val originalIdent = aktørFraDatabase!!.aktørIdent
                    aktørFraDatabase!!.oppdaterAlleFelter(it)
                    lagreEllerOppdaterAktør(aktørFraDatabase!!, originalIdent)
                    return aktørFraDatabase!!
                }
            }
            lagreEllerOppdaterAktør(it, null)
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
        return aktørRepository.findByAktørIdent(aktørIdent.verdi)
            ?: tidligereIdenterRepository.findByTidligereAktoerIdent(aktørIdent.verdi)?.aktør
    }

    fun lagreEllerOppdaterAktør(aktør: Aktør, originalIdent: String?) {
        aktørRepository.save(aktør)
        tidligereIdenterRepository.deleteAllByAktørIs(aktør)
        aktør.tidligereIdenter.forEach {
            it.aktør = aktør
        }
        aktør.dødsbo?.aktør = aktør
        hendelseService.opprettHendelserPåAktør(aktør, originalIdent)
    }

    @Transactional
    fun slettAktoer(aktoerIdDTO: AktoerIdDTO) {
        aktørRepository.deleteAktørByAktørIdent(aktoerIdDTO.aktoerId)
    }
}

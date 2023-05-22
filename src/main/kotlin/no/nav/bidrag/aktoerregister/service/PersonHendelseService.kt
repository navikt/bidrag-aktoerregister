package no.nav.bidrag.aktoerregister.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.KotlinLogging
import no.nav.bidrag.aktoerregister.SECURE_LOGGER
import no.nav.bidrag.aktoerregister.dto.Endringsmelding
import no.nav.bidrag.domain.ident.Ident
import org.springframework.stereotype.Service
import javax.transaction.Transactional

private val LOGGER = KotlinLogging.logger { }

@Service
class PersonHendelseService(
    private val objectMapper: ObjectMapper,
    private val aktørService: AktørService
) {

    @Transactional
    fun behandleHendelse(hendelse: String) {
        SECURE_LOGGER.info("Behandler hendelse: $hendelse")
        val endringsmelding = mapEndringsmelding(hendelse)

        endringsmelding.personidenter.forEach { ident ->
            val aktør = aktørService.hentAktørFraDatabase(Ident(ident))
            aktør?.let {
                SECURE_LOGGER.info("Fant lagret aktør $it. Oppdaterer med nye verdier.")
                val aktørFraPerson = aktørService.hentAktørFraPerson(Ident(ident))
                aktør.oppdaterAlleFelter(aktørFraPerson)
                aktørService.lagreEllerOppdaterAktør(aktør, ident)
                return@forEach
            }
        }
    }

    private fun mapEndringsmelding(hendelse: String): Endringsmelding {
        return try {
            objectMapper.readValue(hendelse, Endringsmelding::class.java)
        } finally {
            LOGGER.info { "Leser endringsmelding: $hendelse" }
        }
    }
}
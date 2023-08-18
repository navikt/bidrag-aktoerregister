package no.nav.bidrag.aktoerregister.service

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.transaction.Transactional
import no.nav.bidrag.aktoerregister.SECURE_LOGGER
import no.nav.bidrag.aktoerregister.dto.Endringsmelding
import no.nav.bidrag.aktoerregister.exception.AktørNotFoundException
import no.nav.bidrag.domain.ident.Ident
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class PersonHendelseService(
    private val objectMapper: ObjectMapper,
    private val aktørService: AktørService
) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(PersonHendelseService::class.java)
    }

    @Transactional
    fun behandleHendelse(hendelse: String) {
        SECURE_LOGGER.info("Behandler hendelse: $hendelse")
        val endringsmelding = mapEndringsmelding(hendelse)

        endringsmelding.personidenter.forEach { ident ->
            val aktør = aktørService.hentAktørFraDatabase(Ident(ident))
            aktør?.let {
                SECURE_LOGGER.info("Fant lagret aktør $it. Oppdaterer med nye verdier.")
                try {
                    val aktørFraPerson = aktørService.hentAktørFraPerson(Ident(ident))
                    aktørService.oppdaterAktør(aktør, aktørFraPerson, ident)
                    return
                } catch (e: AktørNotFoundException) {
                    LOGGER.error("Aktør ikke funnet i bidrag-person! Se sikker logg for mer info.")
                    SECURE_LOGGER.error("Aktør ikke funnet i bidrag-person! Fant ikke person for hendelse: $hendelse")
                }
            }
        }
    }

    private fun mapEndringsmelding(hendelse: String): Endringsmelding {
        return objectMapper.readValue(hendelse, Endringsmelding::class.java)
    }
}

package no.nav.bidrag.aktoerregister.batch.person

import io.github.oshai.KotlinLogging
import no.nav.bidrag.aktoerregister.SECURE_LOGGER
import no.nav.bidrag.aktoerregister.batch.AktørBatchProcessorResult
import no.nav.bidrag.aktoerregister.batch.AktørStatus
import no.nav.bidrag.aktoerregister.consumer.PersonConsumer
import no.nav.bidrag.aktoerregister.persistence.entities.Aktør
import no.nav.bidrag.domain.ident.Ident
import org.springframework.batch.item.ItemProcessor
import org.springframework.core.convert.ConversionService
import org.springframework.stereotype.Component

private val LOGGER = KotlinLogging.logger { }

@Component
class PersonBatchProcessor(
    private val personConsumer: PersonConsumer,
    private val conversionService: ConversionService
) : ItemProcessor<Aktør, AktørBatchProcessorResult?> {

    override fun process(aktør: Aktør): AktørBatchProcessorResult? {
        return try {
            conversionService.convert(personConsumer.hentPerson(Ident(aktør.aktørIdent)), Aktør::class.java)
                .takeIf { it != aktør }
                ?.let {
                    SECURE_LOGGER.debug("Hentet aktør fra Bidrag-Person med id: ${aktør.aktørIdent} og oppdaterer felter på lagret aktør.")
                    aktør.oppdaterAlleFelter(it)
                    AktørBatchProcessorResult(aktør, AktørStatus.UPDATED)
                }
        } catch (e: Exception) {
            LOGGER.error(e) { e.message }
            null
        }
    }
}

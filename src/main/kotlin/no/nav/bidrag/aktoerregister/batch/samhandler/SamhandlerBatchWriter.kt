package no.nav.bidrag.aktoerregister.batch.samhandler

import io.github.oshai.KotlinLogging
import no.nav.bidrag.aktoerregister.batch.AktoerStatus
import no.nav.bidrag.aktoerregister.service.AktoerregisterService
import org.springframework.batch.item.ItemWriter
import org.springframework.stereotype.Component

private val LOGGER = KotlinLogging.logger { }

@Component
class SamhandlerBatchWriter(private val aktoerregisterService: AktoerregisterService) : ItemWriter<SamhandlerBatchProcessorResult> {

    override fun write(samhandlerBatchProcessorResults: List<SamhandlerBatchProcessorResult>) {

        samhandlerBatchProcessorResults
            .filter { it.aktoerStatus == AktoerStatus.UPDATED }
            .map { it.aktør }
            .let {
                LOGGER.trace { "Oppdaterer ${it.size} aktører.." }
                aktoerregisterService.oppdaterAktoerer(it)
            }
    }
}
package no.nav.bidrag.aktoerregister.batch

import io.github.oshai.KotlinLogging
import no.nav.bidrag.aktoerregister.service.AktørregisterService
import org.springframework.batch.item.ItemWriter
import org.springframework.stereotype.Component

private val LOGGER = KotlinLogging.logger { }

@Component
class AktørBatchWriter(private val aktørregisterService: AktørregisterService) : ItemWriter<AktørBatchProcessorResult> {

    override fun write(aktørBatchProcessorResults: List<AktørBatchProcessorResult>) {
        aktørBatchProcessorResults
            .filter { it.aktørStatus == AktørStatus.UPDATED }
            .map { it.aktør }
            .let {
                LOGGER.trace { "Oppdaterer ${it.size} aktører.." }
                aktørregisterService.oppdaterAktører(it)
            }
    }
}

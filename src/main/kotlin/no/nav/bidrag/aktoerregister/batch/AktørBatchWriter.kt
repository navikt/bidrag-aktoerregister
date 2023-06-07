package no.nav.bidrag.aktoerregister.batch

import io.github.oshai.KotlinLogging
import no.nav.bidrag.aktoerregister.service.AktørService
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemWriter
import org.springframework.stereotype.Component

private val LOGGER = KotlinLogging.logger { }

@Component
class AktørBatchWriter(private val aktørService: AktørService) : ItemWriter<AktørBatchProcessorResult> {

    override fun write(chunk: Chunk<out AktørBatchProcessorResult>) {
        chunk
            .filter { it.aktørStatus == AktørStatus.UPDATED }
            .forEach {
                LOGGER.trace { "Oppdaterer aktør ${it.aktør}" }
                aktørService.lagreEllerOppdaterAktør(it.aktør, it.originalIdent)
            }
    }
}

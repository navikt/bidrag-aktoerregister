package no.nav.bidrag.aktoerregister.api

import io.github.oshai.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import no.nav.bidrag.aktoerregister.batch.samhandler.SamhandlerBatchSchedulerConfig
import no.nav.security.token.support.core.api.Protected
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.CompletableFuture

private val LOGGER = KotlinLogging.logger {}

@RestController
@Protected
class BatchController(
    private val samhandlerBatchSchedulerConfig: SamhandlerBatchSchedulerConfig
) {

    @Operation(
        summary = "Start kjøring av TSS batch.",
        description = "TSS batchen startes asynkront. Dette vil medføre at feil under kjøring av batchen ikke vil reflekteres i responskoden dette endepunktet returnerer."
    )
    @ApiResponse(responseCode = "200", description = "TSS batchen ble startet.")
    @PostMapping( "/tssBatch")
    fun startTssBatch(): ResponseEntity<*> {
        CompletableFuture.runAsync {
            try {
                samhandlerBatchSchedulerConfig.scheduleSamhandlerBatch()
            } catch (e: Exception) {
                LOGGER.error(e) { "Manuell start av batchen feilet med følgende feilkode: ${e.message}" }
            }
        }
        return ResponseEntity.ok().build<Any>()
    }
}
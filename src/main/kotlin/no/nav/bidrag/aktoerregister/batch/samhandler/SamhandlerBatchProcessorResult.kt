package no.nav.bidrag.aktoerregister.batch.samhandler


import no.nav.bidrag.aktoerregister.batch.AktoerStatus
import no.nav.bidrag.aktoerregister.persistence.entities.Aktør

data class SamhandlerBatchProcessorResult(
    val aktør: Aktør,
    val aktoerStatus: AktoerStatus
)
package no.nav.bidrag.aktoerregister.consumer

import io.github.oshai.KotlinLogging
import no.nav.bidrag.aktoerregister.SECURE_LOGGER
import no.nav.bidrag.aktoerregister.util.ConsumerUtils.leggTilPathPåUri
import no.nav.bidrag.commons.web.client.AbstractRestClient
import no.nav.bidrag.transport.samhandler.SamhandlerDto
import no.nav.domain.ident.Ident
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestOperations
import java.net.URI

private val LOGGER = KotlinLogging.logger {}

@Service
class SamhandlerConsumer(
    @Value("\${BIDRAG_SAMHANDLER_URL}") val url: URI,
    @Qualifier("azure") private val restTemplate: RestOperations
) : AbstractRestClient(restTemplate, "bidrag-aktoerregister-samhandler") {

    companion object {
        private const val SAMHANDLER_PATH = "/samhandler"
    }

    fun hentSamhandler(samhandlerIdent: String): SamhandlerDto? {
        val response: SamhandlerDto? = postForEntity(leggTilPathPåUri(url, SAMHANDLER_PATH), Ident(samhandlerIdent))
        LOGGER.debug { "Hentet samhandler med fra bidrag-samhandler." }
        SECURE_LOGGER.info { "Hentet samhandler med id: $samhandlerIdent fra bidrag-samhandler." }
        return response
    }
}

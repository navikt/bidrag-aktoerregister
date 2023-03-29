package no.nav.bidrag.aktoerregister.consumer

import io.github.oshai.KotlinLogging
import no.nav.bidrag.aktoerregister.SECURE_LOGGER
import no.nav.bidrag.aktoerregister.util.ConsumerUtils.leggTilPathPåUri
import no.nav.bidrag.commons.web.client.AbstractRestClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestOperations
import person.dto.PersonDetaljerDto
import java.net.URI

private val LOGGER = KotlinLogging.logger {}

@Service
class PersonConsumer(
    @Value("\${BIDRAG_PERSON_URL}") val url: URI,
    @Qualifier("azure") private val restTemplate: RestOperations
) : AbstractRestClient(restTemplate, "bidrag-aktoerregister-samhandler") {

    companion object {
        private const val PERSON_PATH = "/informasjon/detaljer"
    }

    fun hentPerson(personIdent: String): PersonDetaljerDto? {
        val response: PersonDetaljerDto? = postForEntity(leggTilPathPåUri(url, PERSON_PATH), personIdent)
        LOGGER.debug { "Hentet person fra bidrag-person." }
        SECURE_LOGGER.info { "Hentet person med id: $personIdent fra bidrag-person." }
        return response
    }
}

package no.nav.bidrag.aktoerregister.hendelse

import io.github.oshai.KotlinLogging
import no.nav.bidrag.aktoerregister.service.PersonHendelseService
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header

private val LOGGER = KotlinLogging.logger { }

class PersonHendelseListener(
    private val personHendelseService: PersonHendelseService
) {

    @KafkaListener(groupId = "bidrag-aktoerregister", topics = ["\${TOPIC_PERSON_HENDELSE"])
    fun lesHendelse(
        hendelse: String,
        @Header(KafkaHeaders.OFFSET) offset: Long,
        @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String,
        @Header(KafkaHeaders.RECEIVED_PARTITION) partition: Int,
        @Header(KafkaHeaders.GROUP_ID) groupId: String
    ) {
        LOGGER.info {"Lese hendelse fra topic: $topic, offset: $offset, partition: $partition, groupId: $groupId"}
        personHendelseService.behandleHendelse(hendelse)
    }
}
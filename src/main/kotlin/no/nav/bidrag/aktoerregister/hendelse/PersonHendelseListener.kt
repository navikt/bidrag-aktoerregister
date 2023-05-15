package no.nav.bidrag.aktoerregister.hendelse

import no.nav.bidrag.aktoerregister.service.PersonHendelseService
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header

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

    }

}
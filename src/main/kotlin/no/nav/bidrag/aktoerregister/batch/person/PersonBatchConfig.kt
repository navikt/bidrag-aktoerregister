package no.nav.bidrag.aktoerregister.batch.person

import no.nav.bidrag.aktoerregister.batch.AktørBatchProcessorResult
import no.nav.bidrag.aktoerregister.batch.AktørBatchWriter
import no.nav.bidrag.aktoerregister.persistence.entities.Aktør
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableBatchProcessing
class PersonBatchConfig(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
    private val personBatchReader: PersonBatchReader,
    private val aktørBatchWriter: AktørBatchWriter,
    private val personBatchProcessor: PersonBatchProcessor,
) {

    companion object {
        const val PERSON_BATCH_OPPDATERING_JOB = "PERSON_BATCH_OPPDATERING_JOB"
        const val PERSON_OPPDATER_AKTOERER_STEP = "PERSON_OPPDATER_AKTOERER_STEP"
    }

    @Bean
    fun personJob(): Job {
        return jobBuilderFactory[PERSON_BATCH_OPPDATERING_JOB]
            .listener(PersonJobListener())
            .incrementer(RunIdIncrementer())
            .flow(personStep())
            .end()
            .build()
    }

    @Bean
    fun personStep(): Step {
        return stepBuilderFactory[PERSON_OPPDATER_AKTOERER_STEP]
            .chunk<Aktør, AktørBatchProcessorResult>(100)
            .reader(personBatchReader)
            .processor(personBatchProcessor)
            .writer(aktørBatchWriter)
            .build()
    }
}
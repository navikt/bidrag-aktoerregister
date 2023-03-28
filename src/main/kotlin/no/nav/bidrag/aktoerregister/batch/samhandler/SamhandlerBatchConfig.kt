package no.nav.bidrag.aktoerregister.batch.samhandler

import net.javacrumbs.shedlock.core.LockProvider
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider
import no.nav.bidrag.aktoerregister.persistence.entities.Aktør
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.core.task.TaskExecutor
import javax.sql.DataSource

@Configuration
@EnableBatchProcessing
class SamhandlerBatchConfig(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
    private val samhandlerBatchReader: SamhandlerBatchReader,
    private val samhandlerBatchWriter: SamhandlerBatchWriter,
    private val samhandlerBatchProcessor: SamhandlerBatchProcessor
) {

    companion object {
        const val SAMHANDLER_BATCH_OPPDATERING_JOB = "SAMHANDLER_BATCH_OPPDATERING_JOB"
        const val SAMHANDLER_OPPDATER_AKTOERER_STEP = "SAMHANDLER_OPPDATER_AKTOERER_STEP"
    }

    @Bean
    fun createJob(): Job {
        return jobBuilderFactory[SAMHANDLER_BATCH_OPPDATERING_JOB]
            .listener(SamhandlerJobListener())
            .incrementer(RunIdIncrementer())
            .flow(createStep())
            .end()
            .build()
    }

    @Bean
    fun createStep(): Step {
        return stepBuilderFactory[SAMHANDLER_OPPDATER_AKTOERER_STEP]
            .chunk<Aktør, SamhandlerBatchProcessorResult>(100)
            .reader(samhandlerBatchReader)
            .processor(samhandlerBatchProcessor)
            .writer(samhandlerBatchWriter)
            .build()
    }

    @Bean
    fun lockProvider(dataSource: DataSource): LockProvider {
        return JdbcTemplateLockProvider(dataSource, "aktoerregister.shedlock")
    }

    @Bean
    fun taskExecutor(): TaskExecutor {
        return SimpleAsyncTaskExecutor()
    }
}
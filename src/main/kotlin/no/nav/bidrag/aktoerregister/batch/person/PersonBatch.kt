package no.nav.bidrag.aktoerregister.batch.person

import no.nav.bidrag.aktoerregister.batch.person.PersonBatchConfig.Companion.PERSON_BATCH_OPPDATERING_JOB
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.launch.JobOperator
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import java.util.*

@Component
class PersonBatch(
        private val jobLauncher: JobLauncher,
        private val jobOperator: JobOperator,
        @Qualifier("personJob") private val job: Job
) {

    fun startPersonBatch() {
        val jobParameters = JobParametersBuilder()
                .addString("jobIdentifier", "RESTARTABLE_JOB")
                .toJobParameters()
        jobLauncher.run(job, jobParameters)
    }

    fun restartPersonBatch(): ResponseEntity<*> {
        val jobExecutionIds = jobOperator.getRunningExecutions(PERSON_BATCH_OPPDATERING_JOB)
        return if (jobExecutionIds.isNotEmpty()) {
            val latestExecutionId = jobExecutionIds.maxOrNull() ?: throw IllegalStateException("No running executions found.")
            jobOperator.restart(latestExecutionId)
            ResponseEntity.ok().build<Any>()
        } else {
            ResponseEntity.notFound().build<Any>()
        }
    }
}

package no.nav.bidrag.aktoerregister.batch.person

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.launch.JobOperator
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import java.util.*

@Component
class PersonBatch(
        private val jobLauncher: JobLauncher,
        private val jobOperator: JobOperator,
        @Qualifier("personJob") private val job: Job
) {

    fun startPersonBatch() {
        val jobParameters = JobParametersBuilder()
                .addString("time", SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().time))
                .toJobParameters()
        jobLauncher.run(job, jobParameters)
    }

    fun restartPersonBatch(executionId: Long) {
        jobOperator.restart(executionId)
    }
}

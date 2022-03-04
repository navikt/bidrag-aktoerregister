package no.nav.bidrag.aktoerregister.batch;

import javax.sql.DataSource;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class TSSBatchConfig {

  public final JobBuilderFactory jobBuilderFactory;

  public final StepBuilderFactory stepBuilderFactory;

  private final TSSAktoerReader tssAktoerReader;

  private final TSSAktoerWriter tssAktoerWriter;

  private final TSSAktoerProcessor tssAktoerProcessor;

  @Autowired
  public TSSBatchConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, TSSAktoerReader tssAktoerReader,
      TSSAktoerWriter tssAktoerWriter, TSSAktoerProcessor tssAktoerProcessor) {
    this.jobBuilderFactory = jobBuilderFactory;
    this.stepBuilderFactory = stepBuilderFactory;
    this.tssAktoerReader = tssAktoerReader;
    this.tssAktoerWriter = tssAktoerWriter;
    this.tssAktoerProcessor = tssAktoerProcessor;
  }

  @Bean
  public Job createJob() {
    return jobBuilderFactory.get("TSSAktoerUpdatesJob")
        .incrementer(new RunIdIncrementer())
        .flow(createStep()).end().build();
  }

  @Bean
  public Step createStep() {
    return stepBuilderFactory.get("UpdateTSSAktoererStep")
        .<Aktoer, TSSAktoerProcessorResult>chunk(100)
        .reader(tssAktoerReader)
        .processor(tssAktoerProcessor)
        .writer(tssAktoerWriter)
        .build();
  }

  @Bean
  public LockProvider lockProvider(DataSource dataSource) {
    return new JdbcTemplateLockProvider(dataSource, "aktoerregister.shedlock");
  }

}

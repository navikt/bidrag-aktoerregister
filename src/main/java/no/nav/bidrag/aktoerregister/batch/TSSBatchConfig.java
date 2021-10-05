package no.nav.bidrag.aktoerregister.batch;

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
  @Autowired
  public JobBuilderFactory jobBuilderFactory;

  @Autowired
  public StepBuilderFactory stepBuilderFactory;

  @Autowired
  private TSSAktoerReader tssAktoerReader;

  @Autowired
  private TSSAktoerWriter tssAktoerWriter;

  @Autowired
  private TSSAktoerProcessor tssAktoerProcessor;

  @Bean
  public Job createJob() {
    return jobBuilderFactory.get("TSSAktoerUpdatesJob")
        .incrementer(new RunIdIncrementer())
        .flow(createStep()).end().build();
  }

  @Bean
  public Step createStep() {
    return stepBuilderFactory.get("UpdateTSSAktoererStep")
        .<Aktoer, TSSAktoerProcessorResult> chunk(1)
        .reader(tssAktoerReader)
        .processor(tssAktoerProcessor)
        .writer(tssAktoerWriter)
        .build();
  }
}

package no.nav.bidrag.aktoerregister.jobs;

import java.util.concurrent.atomic.AtomicInteger;
import no.nav.bidrag.aktoerregister.exception.MQServiceException;
import no.nav.bidrag.aktoerregister.properties.MQProperties;
import no.nav.bidrag.aktoerregister.service.mq.MQMessageHandler;
import no.nav.bidrag.aktoerregister.service.mq.MQService;
import no.rtv.namespacetps.DistribusjonsMelding;
import org.jobrunr.jobs.annotations.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TPSConsumerJob {

  private final MQService mqService;

  private final MQProperties mqProperties;

  private final MQMessageHandler<DistribusjonsMelding> tpsMessageHandler;

  private static final Logger logger = LoggerFactory.getLogger(TPSConsumerJob.class);

  private final AtomicInteger nrOfInvocations = new AtomicInteger();

  @Autowired
  public TPSConsumerJob(MQService mqService, MQProperties mqProperties, MQMessageHandler<DistribusjonsMelding> tpsMessageHandler) {
    this.mqService = mqService;
    this.mqProperties = mqProperties;
    this.tpsMessageHandler = tpsMessageHandler;
  }

  @Job(name = "TPS kontoendringskonsument", retries = 5)
  public void execute() {
    logger.info("TPS-konsument har startet. Dette er kjøring nr: {}", nrOfInvocations.get());
    try {
      mqService.consume(tpsMessageHandler, mqProperties.getTpsEventQueue(), DistribusjonsMelding.class);
    } catch (MQServiceException e) {
      logger.error("TPS-konsument feilet under kjøring", e);
    } finally {
      nrOfInvocations.incrementAndGet();
      logger.info("TPS-konsument har stoppet");
    }
  }
}

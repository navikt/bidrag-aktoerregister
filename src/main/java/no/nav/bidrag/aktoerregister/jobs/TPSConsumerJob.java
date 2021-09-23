package no.nav.bidrag.aktoerregister.jobs;

import jakarta.xml.bind.JAXBException;
import java.util.concurrent.atomic.AtomicInteger;
import javax.jms.JMSException;
import no.nav.bidrag.aktoerregister.properties.MQProperties;
import no.nav.bidrag.aktoerregister.service.mq.MQMessageHandler;
import no.nav.bidrag.aktoerregister.service.mq.MQService;
import no.nav.bidrag.aktoerregister.service.mq.TPSMessageHandler;
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

  private static final Logger logger = LoggerFactory.getLogger(TPSConsumerJob.class);

  private final AtomicInteger nrOfInvocations = new AtomicInteger();

  @Autowired
  public TPSConsumerJob(MQService mqService, MQProperties mqProperties) {
    this.mqService = mqService;
    this.mqProperties = mqProperties;
  }

  @Job(name = "TPS kontoendringskonsument", retries = 5)
  public void execute() {
    MQMessageHandler<DistribusjonsMelding> tpsMessageHandler = new TPSMessageHandler();
    logger.info("TPS-konsument har startet. Dette er kjøring nr: {}", nrOfInvocations.get());
    try {
      mqService.consume(tpsMessageHandler, mqProperties.getTpsEventQueue(), DistribusjonsMelding.class);
    } catch (JAXBException | JMSException e) {
      logger.error("TPS-konsument feilet under kjøring", e);
    } finally {
      nrOfInvocations.incrementAndGet();
      logger.info("TPS-konsument har stoppet");
    }
  }
}

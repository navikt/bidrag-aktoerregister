package no.nav.bidrag.aktoerregister.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.concurrent.CompletableFuture;
import no.nav.bidrag.aktoerregister.batch.TSSBatchSchedulerConfig;
import no.nav.security.token.support.core.api.ProtectedWithClaims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ProtectedWithClaims(
    issuer = "maskinporten",
    claimMap = {"scope=nav:bidrag:aktoerregister.read"})
public class BatchController {

  private final TSSBatchSchedulerConfig tssBatchSchedulerConfig;
  private final Logger LOGGER = LoggerFactory.getLogger(BatchController.class);

  @Autowired
  public BatchController(TSSBatchSchedulerConfig tssBatchSchedulerConfig) {
    this.tssBatchSchedulerConfig = tssBatchSchedulerConfig;
  }

  @Operation(summary = "Start kjøring av TSS batch.",
      description = "TSS batchen startes asynkront. Dette vil medføre at feil under kjøring av batchen ikke vil reflekteres i responskoden dette endepunktet returnerer.")
  @ApiResponse(responseCode = "200", description = "TSS batchen ble startet.")
  @PostMapping(path = "/tssBatch")
  public ResponseEntity<?> startTssBatch() {
    CompletableFuture.runAsync(
        () -> {
          try {
            tssBatchSchedulerConfig.scheduleTSSBatch();
          } catch (Exception e) {
            LOGGER.error(
                "Manuell start av batchen feilet med følgende feilkode: {}", e.getMessage());
          }
        });
    return ResponseEntity.ok().build();
  }
}

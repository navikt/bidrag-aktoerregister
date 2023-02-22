package no.nav.bidrag.aktoerregister.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import no.nav.bidrag.aktoerregister.batch.TSSBatchSchedulerConfig;
import no.nav.security.token.support.core.api.ProtectedWithClaims;
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

  @Autowired
  public BatchController(TSSBatchSchedulerConfig tssBatchSchedulerConfig) {
    this.tssBatchSchedulerConfig = tssBatchSchedulerConfig;
  }

  @Operation(summary = "Start kjøring av TSS batch.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "TSS batchen ble startet."),
    @ApiResponse(responseCode = "500", description = "Noe gikk galt under start av batchen.")
  })
  @PostMapping(path = "/tssBatch")
  public ResponseEntity<?> startTssBatch() {
    try {
      tssBatchSchedulerConfig.scheduleTSSBatch();
    } catch (Exception e) {
      return ResponseEntity.internalServerError()
          .body("Start av batchen feilet med følgende feilkode: " + e.getMessage());
    }
    return ResponseEntity.ok().build();
  }
}

package no.nav.bidrag.aktoerregister.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import no.nav.bidrag.aktoerregister.dto.AktoerDTO;
import no.nav.bidrag.aktoerregister.dto.AktoerIdDTO;
import no.nav.bidrag.aktoerregister.dto.HendelseDTO;
import no.nav.bidrag.aktoerregister.exception.AktoerNotFoundException;
import no.nav.bidrag.aktoerregister.service.AktoerregisterService;
import no.nav.security.token.support.core.api.ProtectedWithClaims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@ProtectedWithClaims(
    issuer = "maskinporten",
    claimMap = {"scope=nav:bidrag:aktoerregister.read"})
public class AktoerregisterController {

  private final AktoerregisterService aktoerregisterService;

  @Autowired
  public AktoerregisterController(AktoerregisterService aktoerregisterService) {
    this.aktoerregisterService = aktoerregisterService;
  }

  @Operation(
      summary = "Hent informasjon om gitt aktør.",
      description =
          "For personer returneres kun kontonummer. "
              + "For andre typer aktører leveres også navn og adresse.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Aktøren ble funnet."),
    @ApiResponse(
        responseCode = "400",
        description = "Gitt identtype eller ident er ugyldig.",
        content = @Content()),
    @ApiResponse(
        responseCode = "404",
        description = "Ingen aktør med gitt identtype og ident ble funnet.",
        content = @Content())
  })
  @PostMapping(path = "/aktoer", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AktoerDTO> hentAktoer(@RequestBody AktoerIdDTO request) {
    try {
      AktoerDTO aktoer = aktoerregisterService.hentAktoer(request);
      return ResponseEntity.ok(aktoer);
    } catch (AktoerNotFoundException e) {
      log.info("aktoer {} ikke funnet", request.getAktoerId());
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "Finner ingen aktør med oppgitt ident", e);
    } catch (Exception e) {
      log.info("Feil ved henting av aktør {}", request.getAktoerId(), e);
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          "Intern tjenestefeil. Problem med oppkobling mot MQ. Prøv igjen senere.",
          e);
    }
  }

  @Operation(
      summary = "Tilbyr en liste over aktøroppdateringer.",
      description =
          """
              Ingen informasjon om aktøren leveres av denne tjenesten utover aktørId'n.
              Hendelsene legges inn med stigende sekvensnummer. Klienten må selv ta vare på hvilke sekvensnummer som sist er behandlet, og be om å få hendelser fra det neste sekvensnummeret ved neste kall.
              Dersom det ikke returneres noen hendelser er ingen av aktørene endret siden siste kall. Samme sekvensnummer må da benyttes i neste kall.

              Nye hendelser vil alltid ha høyere sekvensnummer enn tidligere hendelser.
              Det kan forekomme hull i sekvensnummer-rekken.
              Dersom det kommer en hendelse for en aktør med tidligere hendelser (lavere sekvensnummer) er det ikke garantert at de tidligere hendelsene ikke returneres.""")
  @GetMapping(path = "/hendelser", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<HendelseDTO>> hentHendelser(
      @Parameter(description = "Angir første sekvensnummer som ønskes hentet. Default-verdi er 0")
          @RequestParam(name = "fraSekvensnummer", defaultValue = "0")
          Integer fraSekvensnummer,
      @Parameter(
              description = "Maksimalt antall hendelser som ønskes hentet. Default-verdi er 1000.")
          @RequestParam(name = "antall", defaultValue = "1000")
          Integer antall) {
    try {
      List<HendelseDTO> hendelser = aktoerregisterService.hentHendelser(fraSekvensnummer, antall);

      return ResponseEntity.ok(hendelser);
    } catch (Exception e) {
      log.error("Feil ved henting av {} hendelser fra sekvensnummer {}", antall, fraSekvensnummer);
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          "Intern tjenestefeil. Problem ved henting av hendelser. Prøv igjen senere",
          e);
    }
  }
}

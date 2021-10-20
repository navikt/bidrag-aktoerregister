package no.nav.bidrag.aktoerregister.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import no.nav.bidrag.aktoerregister.domene.AktoerDTO;
import no.nav.bidrag.aktoerregister.domene.AktoerIdDTO;
import no.nav.bidrag.aktoerregister.domene.HendelseDTO;
import no.nav.bidrag.aktoerregister.domene.IdenttypeDTO;
import no.nav.bidrag.aktoerregister.exception.AktoerNotFoundException;
import no.nav.bidrag.aktoerregister.exception.MQServiceException;
import no.nav.bidrag.aktoerregister.exception.TPSServiceException;
import no.nav.bidrag.aktoerregister.exception.TSSServiceException;
import no.nav.bidrag.aktoerregister.service.AktoerregisterService;
import no.nav.security.token.support.core.api.ProtectedWithClaims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@ProtectedWithClaims(issuer = "maskinporten", claimMap = {"scope=nav:bidrag:aktoerregister.read"})
public class AktoerregisterController {

  private final AktoerregisterService aktoerregisterService;

  @Autowired
  public AktoerregisterController(AktoerregisterService aktoerregisterService) {
    this.aktoerregisterService = aktoerregisterService;
  }

  @Operation(summary = "Hent informasjon om gitt aktør.", description = "For personer returneres kun kontonummer. "
      + "For andre typer aktører leveres også navn og adresse.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Aktøren ble funnet."),
      @ApiResponse(responseCode = "400", description = "Gitt identtype eller ident er ugyldig.", content = @Content()),
      @ApiResponse(responseCode = "404", description = "Ingen aktør med gitt identtype og ident ble funnet.", content = @Content())
  })
  @GetMapping("/aktoer/{identtype}/{ident}")
  public ResponseEntity<AktoerDTO> hentAktoer(
      @Parameter(description = "Angir hvilken type ident som er angitt i forespørselen. "
          + "For personer vil dette være FNR eller DNR, som angis med PERSONNUMMER. "
          + "Utover dette benyttes AKTOERNUMMER.") @PathVariable(name = "identtype") IdenttypeDTO identtype,

      @Parameter(description = "Identen for aktøren som skal hentes. "
          + "For personer vil dette være FNR eller DNR. "
          + "Ellers benyttes aktørnummer på elleve siffer hvor første siffer er 8.") @PathVariable(name = "ident") String ident)
      throws ResponseStatusException {

    try {
      AktoerDTO aktoer = aktoerregisterService.hentAktoer(new AktoerIdDTO(ident, identtype));
      return ResponseEntity.ok(aktoer);
    } catch (AktoerNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Finner ingen aktør med oppgitt ident", e);
    } catch (MQServiceException | TSSServiceException | TPSServiceException e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Intern tjenestefeil. Problem med oppkobling mot MQ. Prøv igjen senere.", e);
    }
  }

  @Operation(summary = "Tilbyr en liste over aktøroppdateringer.", description = "Ingen informasjon om aktøren leveres av denne tjenesten utover aktørId'n. Hendelsene legges inn med stigende sekvensnummer."
      + "Klienten må selv ta vare på hvilke sekvensnummer som sist er behandlet, og be om å få hendelser fra det neste sekvensnummeret ved neste kall."
      + "Dersom det ikke returneres noen hendelser er ingen av aktørene endret siden siste kall. Samme sekvensnummer må da benyttes i neste kall."
      + "\n\n"
      + "Nye hendelser vil alltid ha høyere sekvensnummer enn tidligere hendelser. Det kan forekomme hull i sekvensnummer-rekken."
      + "Dersom det kommer en hendelse for en aktør med tidligere hendelser (lavere sekvensnummer) er det ikke garantert at de tidligere hendelsene ikke returneres.")
  @GetMapping("/hendelser")
  public ResponseEntity<List<HendelseDTO>> hentHendelser(
      @Parameter(description = "Angir første sekvensnummer som ønskes hentet. Default-verdi er 0")
      @RequestParam(name = "fraSekvensnummer", defaultValue = "0") Integer fraSekvensnummer,

      @Parameter(description="Maksimalt antall hendelser som ønskes hentet. Default-verdi er 1000.")
      @RequestParam(name = "antall", defaultValue = "1000") Integer antall)  throws ResponseStatusException {

    try {
      List<HendelseDTO> hendelser = aktoerregisterService
          .hentHendelser(fraSekvensnummer, antall);

      return ResponseEntity.ok(hendelser);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Intern tjenestefeil. Problem ved henting av hendelser. Prøv igjen senere", e);
    }
  }
}

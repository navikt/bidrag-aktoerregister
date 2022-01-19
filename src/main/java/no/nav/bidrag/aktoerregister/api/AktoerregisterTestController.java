package no.nav.bidrag.aktoerregister.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import no.nav.bidrag.aktoerregister.domene.AktoerIdDTO;
import no.nav.bidrag.aktoerregister.domene.IdenttypeDTO;
import no.nav.bidrag.aktoerregister.domene.PersonDTO;
import no.nav.bidrag.aktoerregister.exception.MQServiceException;
import no.nav.bidrag.aktoerregister.service.PDLService;
import no.nav.bidrag.aktoerregister.service.TPSService;
import no.nav.bidrag.aktoerregister.service.TSSService;
import no.nav.security.token.support.core.api.ProtectedWithClaims;
import no.nav.security.token.support.core.api.Unprotected;
import no.rtv.namespacetps.TpsPersonData;
import no.rtv.namespacetss.TssSamhandlerData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@ProtectedWithClaims(issuer = "maskinporten", claimMap = {"scope=nav:bidrag:aktoerregister.read"})
public class AktoerregisterTestController {

  private final TPSService tpsService;

  private final TSSService tssService;

  private final PDLService pdlService;

  @Autowired
  public AktoerregisterTestController(TPSService tpsService, TSSService tssService, PDLService pdlService) {
    this.tpsService = tpsService;
    this.tssService = tssService;
    this.pdlService = pdlService;
  }

  @Operation(summary = "Hent informasjon om gitt aktør.", description = "For personer returneres kun kontonummer. "
      + "For andre typer aktører leveres også navn og adresse.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Aktøren ble funnet."),
      @ApiResponse(responseCode = "400", description = "Gitt identtype eller ident er ugyldig.", content = @Content()),
      @ApiResponse(responseCode = "404", description = "Ingen aktør med gitt identtype og ident ble funnet.", content = @Content())
  })
  @GetMapping("/tss/{ident}")
  public ResponseEntity<TssSamhandlerData> hentTSSAktoer(
      @Parameter(description = "Identen for aktøren som skal hentes. "
          + "For personer vil dette være FNR eller DNR. "
          + "Ellers benyttes aktørnummer på elleve siffer hvor første siffer er 8.") @PathVariable(name = "ident") String ident)
      throws ResponseStatusException {

    try {
      TssSamhandlerData tssSamhandlerData =  tssService.hentRawAktoer(new AktoerIdDTO(ident, IdenttypeDTO.AKTOERNUMMER));
      return ResponseEntity.ok(tssSamhandlerData);
    } catch (MQServiceException e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Intern tjenestefeil. Problem med oppkobling mot MQ. Prøv igjen senere.", e);
    }
  }

  @Operation(summary = "Hent informasjon om gitt aktør.", description = "For personer returneres kun kontonummer. "
      + "For andre typer aktører leveres også navn og adresse.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Aktøren ble funnet."),
      @ApiResponse(responseCode = "400", description = "Gitt identtype eller ident er ugyldig.", content = @Content()),
      @ApiResponse(responseCode = "404", description = "Ingen aktør med gitt identtype og ident ble funnet.", content = @Content())
  })
  @GetMapping("/tps/{ident}")
  public ResponseEntity<TpsPersonData> hentTPSAktoer(
      @Parameter(description = "Identen for aktøren som skal hentes. "
          + "For personer vil dette være FNR eller DNR. "
          + "Ellers benyttes aktørnummer på elleve siffer hvor første siffer er 8.") @PathVariable(name = "ident") String ident)
      throws ResponseStatusException {

    try {
      TpsPersonData tpsPersonData =  tpsService.hentRawAktoer(new AktoerIdDTO(ident, IdenttypeDTO.PERSONNUMMER));
      return ResponseEntity.ok(tpsPersonData);
    } catch (MQServiceException e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Intern tjenestefeil. Problem med oppkobling mot MQ. Prøv igjen senere.", e);
    }
  }

  @GetMapping("/pdl/{ident}")
  @Unprotected
  public ResponseEntity<PersonDTO> hentPDLAktoer(
      @Parameter(description = "Identen for aktøren som skal hentes. "
          + "For personer vil dette være FNR eller DNR. "
          + "Ellers benyttes aktørnummer på elleve siffer hvor første siffer er 8.") @PathVariable(name = "ident") String ident)
      throws ResponseStatusException {

      PersonDTO personDTO =  pdlService.hentRawAktoer(ident);
      return ResponseEntity.ok(personDTO);
  }
}

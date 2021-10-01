package no.nav.bidrag.aktoerregister.api;

import io.swagger.v3.oas.annotations.Parameter;
import no.nav.bidrag.aktoerregister.domene.AktoerDTO;
import no.nav.bidrag.aktoerregister.domene.AktoerIdDTO;
import no.nav.bidrag.aktoerregister.domene.IdenttypeDTO;
import no.nav.bidrag.aktoerregister.exception.AktoerNotFoundException;
import no.nav.bidrag.aktoerregister.exception.MQServiceException;
import no.nav.bidrag.aktoerregister.exception.TSSServiceException;
import no.nav.bidrag.aktoerregister.service.TSSService;
import no.nav.security.token.support.core.api.Unprotected;
import no.rtv.namespacetss.TssSamhandlerData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tss")
//@ProtectedWithClaims(issuer = "maskinporten", claimMap = {"scope=nav:bidrag:aktoerregister.read"})
@Unprotected
public class TSSTestController {

  private final TSSService tssService;

  @Autowired
  public TSSTestController(TSSService tssService) {
    this.tssService = tssService;
  }

  @GetMapping("/aktoer/{identtype}/{ident}")
  public ResponseEntity<AktoerDTO> getAktoer(
      @Parameter(description = "Angir hvilken type ident som er angitt i forespørselen. "
          + "For personer vil dette være FNR eller DNR, som angis med PERSONNUMMER. "
          + "Utover dette benyttes AKTOERNUMMER.") @PathVariable(name = "identtype") IdenttypeDTO identtype,

      @Parameter(description = "Identen for aktøren som skal hentes. "
          + "For personer vil dette være FNR eller DNR. "
          + "Ellers benyttes aktørnummer på elleve siffer hvor første siffer er 8.") @PathVariable(name = "ident") String ident)
//      @Parameter(in = ParameterIn.HEADER, name = "Token", description = "Maskinporten JWT token", required = true) String token)
      throws MQServiceException, TSSServiceException, AktoerNotFoundException {
    AktoerIdDTO aktoerId = new AktoerIdDTO(ident, identtype);
    AktoerDTO aktoer = tssService.hentAktoer(aktoerId);
    return ResponseEntity.ok(aktoer);
  }

  @GetMapping("/samhandler/{identtype}/{ident}")
  public ResponseEntity<TssSamhandlerData> getSamhandler(
      @Parameter(description = "Angir hvilken type ident som er angitt i forespørselen. "
          + "For personer vil dette være FNR eller DNR, som angis med PERSONNUMMER. "
          + "Utover dette benyttes AKTOERNUMMER.") @PathVariable(name = "identtype") IdenttypeDTO identtype,

      @Parameter(description = "Identen for aktøren som skal hentes. "
          + "For personer vil dette være FNR eller DNR. "
          + "Ellers benyttes aktørnummer på elleve siffer hvor første siffer er 8.") @PathVariable(name = "ident") String ident)
//      @Parameter(in = ParameterIn.HEADER, name = "Token", description = "Maskinporten JWT token", required = true) String token)
      throws MQServiceException {
    AktoerIdDTO aktoerId = new AktoerIdDTO(ident, identtype);
    TssSamhandlerData tssSamhandlerData = tssService.hentSamhandler(aktoerId);
    return ResponseEntity.ok(tssSamhandlerData);
  }

}

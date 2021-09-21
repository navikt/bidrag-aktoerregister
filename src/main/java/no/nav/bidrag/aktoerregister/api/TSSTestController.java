package no.nav.bidrag.aktoerregister.api;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.xml.bind.JAXBException;
import javax.jms.JMSException;
import no.nav.bidrag.aktoerregister.domene.Aktoer;
import no.nav.bidrag.aktoerregister.domene.AktoerId;
import no.nav.bidrag.aktoerregister.domene.Identtype;
import no.nav.bidrag.aktoerregister.service.TSSTestService;
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

  private final TSSTestService tssTestService;

  @Autowired
  public TSSTestController(TSSTestService tssTestService) {
    this.tssTestService = tssTestService;
  }

  @GetMapping("/aktoer/{identtype}/{ident}")
  public ResponseEntity<Aktoer> getAktoer(
      @Parameter(description = "Angir hvilken type ident som er angitt i forespørselen. "
          + "For personer vil dette være FNR eller DNR, som angis med PERSONNUMMER. "
          + "Utover dette benyttes AKTOERNUMMER.") @PathVariable(name = "identtype") Identtype identtype,

      @Parameter(description = "Identen for aktøren som skal hentes. "
          + "For personer vil dette være FNR eller DNR. "
          + "Ellers benyttes aktørnummer på elleve siffer hvor første siffer er 8.") @PathVariable(name = "ident") String ident)
//      @Parameter(in = ParameterIn.HEADER, name = "Token", description = "Maskinporten JWT token", required = true) String token)
      throws JAXBException, JMSException {
    AktoerId aktoerId = new AktoerId(ident, identtype);
    Aktoer aktoer = tssTestService.hentAktoer(aktoerId);
    return ResponseEntity.ok(aktoer);
  }

  @GetMapping("/samhandler/{identtype}/{ident}")
  public ResponseEntity<TssSamhandlerData> getTssSamhandler(
      @Parameter(description = "Angir hvilken type ident som er angitt i forespørselen. "
          + "For personer vil dette være FNR eller DNR, som angis med PERSONNUMMER. "
          + "Utover dette benyttes AKTOERNUMMER.") @PathVariable(name = "identtype") Identtype identtype,

      @Parameter(description = "Identen for aktøren som skal hentes. "
          + "For personer vil dette være FNR eller DNR. "
          + "Ellers benyttes aktørnummer på elleve siffer hvor første siffer er 8.") @PathVariable(name = "ident") String ident)
      throws JAXBException, JMSException {
    AktoerId aktoerId = new AktoerId(ident, identtype);
    TssSamhandlerData tssSamhandlerData = tssTestService.hentTssSamhandler(aktoerId);
    return ResponseEntity.ok(tssSamhandlerData);
  }

}

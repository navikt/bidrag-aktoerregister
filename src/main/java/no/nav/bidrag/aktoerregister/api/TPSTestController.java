package no.nav.bidrag.aktoerregister.api;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.xml.bind.JAXBException;
import java.util.concurrent.TimeoutException;
import javax.jms.JMSException;
import no.nav.bidrag.aktoerregister.domene.AktoerDTO;
import no.nav.bidrag.aktoerregister.domene.AktoerIdDTO;
import no.nav.bidrag.aktoerregister.domene.IdenttypeDTO;
import no.nav.bidrag.aktoerregister.exception.AktoerNotFoundException;
import no.nav.bidrag.aktoerregister.exception.MQServiceException;
import no.nav.bidrag.aktoerregister.exception.TPSServiceException;
import no.nav.bidrag.aktoerregister.service.TPSService;
import no.nav.security.token.support.core.api.Unprotected;
import no.rtv.namespacetps.TpsPersonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tps")
@Unprotected
public class TPSTestController {

  private final TPSService tpsService;

  @Autowired
  public TPSTestController(TPSService tpsService) {
    this.tpsService = tpsService;
  }

  @GetMapping("/aktoer/{identtype}/{ident}")
  public ResponseEntity<AktoerDTO> getAktoer(
      @Parameter(description = "Angir hvilken type ident som er angitt i forespørselen. "
          + "For personer vil dette være FNR eller DNR, som angis med PERSONNUMMER. "
          + "Utover dette benyttes AKTOERNUMMER.") @PathVariable(name = "identtype") IdenttypeDTO identtype,

      @Parameter(description = "Identen for aktøren som skal hentes. "
          + "For personer vil dette være FNR eller DNR. "
          + "Ellers benyttes aktørnummer på elleve siffer hvor første siffer er 8.") @PathVariable(name = "ident") String ident)
      throws JAXBException, JMSException, TimeoutException, MQServiceException, AktoerNotFoundException, TPSServiceException {
    AktoerIdDTO aktoerId = new AktoerIdDTO(ident, identtype);
    AktoerDTO aktoer = tpsService.hentAktoer(aktoerId);
    return ResponseEntity.ok(aktoer);
  }

  @GetMapping("/persondata/{identtype}/{ident}")
  public ResponseEntity<TpsPersonData> getTpsPersonData(
      @Parameter(description = "Angir hvilken type ident som er angitt i forespørselen. "
          + "For personer vil dette være FNR eller DNR, som angis med PERSONNUMMER. "
          + "Utover dette benyttes AKTOERNUMMER.") @PathVariable(name = "identtype") IdenttypeDTO identtype,

      @Parameter(description = "Identen for aktøren som skal hentes. "
          + "For personer vil dette være FNR eller DNR. "
          + "Ellers benyttes aktørnummer på elleve siffer hvor første siffer er 8.") @PathVariable(name = "ident") String ident)
      throws JAXBException, JMSException, TimeoutException, MQServiceException {
    AktoerIdDTO aktoerId = new AktoerIdDTO(ident, identtype);
    TpsPersonData tpsPersonData = tpsService.hentTpsPersonData(aktoerId);
    return ResponseEntity.ok(tpsPersonData);
  }
}

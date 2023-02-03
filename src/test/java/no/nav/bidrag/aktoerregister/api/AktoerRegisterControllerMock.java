package no.nav.bidrag.aktoerregister.api;

import java.util.List;
import no.nav.bidrag.aktoerregister.domene.AktoerDTO;
import no.nav.bidrag.aktoerregister.domene.AktoerIdDTO;
import no.nav.bidrag.aktoerregister.domene.HendelseDTO;
import no.nav.bidrag.aktoerregister.domene.IdenttypeDTO;
import no.nav.bidrag.aktoerregister.persistence.entities.Adresse;
import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;
import no.nav.bidrag.aktoerregister.persistence.repository.AktoerRepository;
import no.nav.bidrag.aktoerregister.persistence.repository.HendelseRepository;
import no.nav.bidrag.aktoerregister.service.AktoerregisterService;
import no.nav.bidrag.aktoerregister.service.AktoerregisterServiceImpl;
import no.nav.bidrag.aktoerregister.service.AktoerService;
import no.nav.bidrag.aktoerregister.service.TPSServiceMock;
import no.nav.bidrag.aktoerregister.service.TSSServiceMock;
import no.nav.security.token.support.core.api.Unprotected;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aktoermock")
@Unprotected
public class AktoerRegisterControllerMock {

  private final AktoerregisterService aktoerregisterService;

  @Autowired
  public AktoerRegisterControllerMock(
      AktoerRepository aktoerRepository, HendelseRepository hendelseRepository) {
    AktoerService tssService = new TSSServiceMock();
    AktoerService tpsService = new TPSServiceMock();
    this.aktoerregisterService =
        new AktoerregisterServiceImpl(aktoerRepository, hendelseRepository, tpsService, tssService);
  }

  @GetMapping("/{fnr}")
  public ResponseEntity<AktoerDTO> hentAktoer(@PathVariable(name = "fnr") String fnr) {
    AktoerIdDTO aktoerIdDTO = new AktoerIdDTO();
    aktoerIdDTO.setAktoerId(fnr);
    aktoerIdDTO.setIdenttype(IdenttypeDTO.PERSONNUMMER);
    return ResponseEntity.ok(aktoerregisterService.hentAktoer(aktoerIdDTO));
  }

  @GetMapping("/hendelser/{fra}/{antall}")
  public ResponseEntity<List<HendelseDTO>> hentHendelser(
      @PathVariable(name = "fra") int fra, @PathVariable(name = "antall") int antall) {
    return ResponseEntity.ok(aktoerregisterService.hentHendelser(fra, antall));
  }

  @GetMapping("/oppdater/{fnr}")
  public ResponseEntity<String> oppdaterAktoer(@PathVariable(name = "fnr") String fnr) {
    Aktoer aktoer = new Aktoer();
    aktoer.setAktoerIdent(fnr);
    aktoer.setAktoerType(IdenttypeDTO.PERSONNUMMER.name());

    Adresse adresse = new Adresse();
    adresse.setAdresselinje1("Testgate 1");
    aktoer.setAdresse(adresse);
    aktoerregisterService.oppdaterAktoer(aktoer);
    return ResponseEntity.ok("Oppdatert + " + fnr);
  }
}

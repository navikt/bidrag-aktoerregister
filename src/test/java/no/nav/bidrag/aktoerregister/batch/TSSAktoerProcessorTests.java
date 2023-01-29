package no.nav.bidrag.aktoerregister.batch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;

import no.nav.bidrag.aktoerregister.domene.AdresseDTO;
import no.nav.bidrag.aktoerregister.domene.AktoerDTO;
import no.nav.bidrag.aktoerregister.domene.AktoerIdDTO;
import no.nav.bidrag.aktoerregister.domene.IdenttypeDTO;
import no.nav.bidrag.aktoerregister.exception.AktoerNotFoundException;
import no.nav.bidrag.aktoerregister.exception.MQServiceException;
import no.nav.bidrag.aktoerregister.exception.TSSServiceException;
import no.nav.bidrag.aktoerregister.persistence.entities.Adresse;
import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;
import no.nav.bidrag.aktoerregister.service.TSSService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TSSAktoerProcessorTests {

  @Mock private TSSService tssService;

  private TSSAktoerProcessor tssAktoerProcessor;

  @BeforeEach
  public void SetUp() {
    this.tssAktoerProcessor = new TSSAktoerProcessor(tssService);
  }

  @Test
  public void TestAktoerFromTssUpdated()
      throws MQServiceException, TSSServiceException, AktoerNotFoundException {

    Aktoer aktoer = new Aktoer();
    aktoer.setAktoerId("1234");
    aktoer.setAktoerType(IdenttypeDTO.PERSONNUMMER.name());

    AktoerDTO tssAktoer = new AktoerDTO();
    AktoerIdDTO tssAktoerIdDTO = new AktoerIdDTO("1234", IdenttypeDTO.PERSONNUMMER);
    tssAktoer.setAktoerId(tssAktoerIdDTO);
    AdresseDTO adresseDTO = new AdresseDTO();
    adresseDTO.setAdresselinje1("Testgate 1");
    tssAktoer.setAdresse(adresseDTO);

    Mockito.when(tssService.hentAktoer(any())).thenReturn(tssAktoer);

    TSSAktoerProcessorResult tssAktoerProcessorResult = tssAktoerProcessor.process(aktoer);
    Aktoer updatedAktoer = tssAktoerProcessorResult.getAktoer();

    assertNotNull(updatedAktoer);
    assertEquals("Testgate 1", updatedAktoer.getAdresse().getAdresselinje1());
    assertEquals(AktoerStatus.UPDATED, tssAktoerProcessorResult.getAktoerStatus());
  }

  @Test
  public void TestAktoerFromTssNotUpdated()
      throws MQServiceException, TSSServiceException, AktoerNotFoundException {
    Aktoer aktoer = new Aktoer();
    aktoer.setAktoerId("1234");
    aktoer.setAktoerType(IdenttypeDTO.PERSONNUMMER.name());

    Adresse adresse = new Adresse();
    adresse.setAdresselinje1("Testgate 1");

    aktoer.setAdresse(adresse);

    AktoerDTO tssAktoer = new AktoerDTO();
    AktoerIdDTO tssAktoerIdDTO = new AktoerIdDTO("1234", IdenttypeDTO.PERSONNUMMER);
    tssAktoer.setAktoerId(tssAktoerIdDTO);
    AdresseDTO adresseDTO = new AdresseDTO();
    adresseDTO.setAdresselinje1("Testgate 1");
    tssAktoer.setAdresse(adresseDTO);

    Mockito.when(tssService.hentAktoer(any())).thenReturn(tssAktoer);

    TSSAktoerProcessorResult tssAktoerProcessorResult = tssAktoerProcessor.process(aktoer);
    Aktoer updatedAktoer = tssAktoerProcessorResult.getAktoer();

    assertNull(updatedAktoer);
    assertEquals(AktoerStatus.NOT_UPDATED, tssAktoerProcessorResult.getAktoerStatus());
  }

  @Test
  public void TestAkterNotFound()
      throws MQServiceException, TSSServiceException, AktoerNotFoundException {
    Aktoer aktoer = new Aktoer();
    aktoer.setAktoerId("1234");
    aktoer.setAktoerType(IdenttypeDTO.PERSONNUMMER.name());

    Mockito.when(tssService.hentAktoer(any())).thenThrow(new AktoerNotFoundException(""));

    TSSAktoerProcessorResult tssAktoerProcessorResult = tssAktoerProcessor.process(aktoer);
    Aktoer updatedAktoer = tssAktoerProcessorResult.getAktoer();

    assertNull(updatedAktoer);
    assertEquals(AktoerStatus.NOT_FOUND, tssAktoerProcessorResult.getAktoerStatus());
  }
}

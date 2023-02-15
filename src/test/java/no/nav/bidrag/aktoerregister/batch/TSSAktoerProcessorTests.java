package no.nav.bidrag.aktoerregister.batch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import no.nav.bidrag.aktoerregister.domene.enumer.IdenttypeDTO;
import no.nav.bidrag.aktoerregister.exception.AktoerNotFoundException;
import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;
import no.nav.bidrag.aktoerregister.service.AktoerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TSSAktoerProcessorTests {

  private final String adresse = "Testgate 1";
  @Mock private AktoerService tssService;
  @InjectMocks private TSSAktoerProcessor tssAktoerProcessor;
  private Aktoer aktoer;
  private Aktoer aktoerFraTss;

  @BeforeEach
  public void setUp() {
    String ident = "1234";
    aktoer =
        Aktoer.builder().aktoerIdent(ident).aktoerType(IdenttypeDTO.PERSONNUMMER.name()).build();

    aktoerFraTss =
        Aktoer.builder()
            .aktoerIdent(ident)
            .aktoerType(IdenttypeDTO.PERSONNUMMER.name())
            .adresselinje1(adresse)
            .build();
  }

  @Test
  public void skalOppdatereAktoerFraTss() {
    when(tssService.hentAktoer(any())).thenReturn(aktoerFraTss);

    TSSAktoerProcessorResult tssAktoerProcessorResult = tssAktoerProcessor.process(aktoer);

    assertNotNull(tssAktoerProcessorResult);
    assertNotNull(tssAktoerProcessorResult.getAktoer());
    assertEquals(adresse, tssAktoerProcessorResult.getAktoer().getAdresselinje1());
    assertEquals(AktoerStatus.UPDATED, tssAktoerProcessorResult.getAktoerStatus());
  }

  @Test
  public void skalIkkeOppdatereAktoerFraTss() {
    aktoer.setAdresselinje1(adresse);

    when(tssService.hentAktoer(any())).thenReturn(aktoerFraTss);

    TSSAktoerProcessorResult tssAktoerProcessorResult = tssAktoerProcessor.process(aktoer);

    assertNotNull(tssAktoerProcessorResult);
    assertNull(tssAktoerProcessorResult.getAktoer());
    assertEquals(AktoerStatus.NOT_UPDATED, tssAktoerProcessorResult.getAktoerStatus());
  }

  @Test
  public void TestAkterNotFound() {
    when(tssService.hentAktoer(any())).thenThrow(new AktoerNotFoundException(""));

    TSSAktoerProcessorResult tssAktoerProcessorResult = tssAktoerProcessor.process(aktoer);

    assertNotNull(tssAktoerProcessorResult);
    assertNull(tssAktoerProcessorResult.getAktoer());
    assertEquals(AktoerStatus.NOT_FOUND, tssAktoerProcessorResult.getAktoerStatus());
  }
}

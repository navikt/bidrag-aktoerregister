package no.nav.bidrag.aktoerregister.service;

import static no.nav.bidrag.felles.test.data.konto.TestKontoBuilder.konto;
import static no.nav.bidrag.felles.test.data.person.TestPersonBuilder.person;
import static no.nav.bidrag.felles.test.data.samhandler.TestSamhandlerBuilder.samhandler;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import no.nav.bidrag.aktoerregister.converter.AktoerTilAktoerDTOConverter;
import no.nav.bidrag.aktoerregister.domene.AktoerDTO;
import no.nav.bidrag.aktoerregister.domene.AktoerIdDTO;
import no.nav.bidrag.aktoerregister.domene.HendelseDTO;
import no.nav.bidrag.aktoerregister.domene.enumer.IdenttypeDTO;
import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;
import no.nav.bidrag.aktoerregister.repository.AktoerRepositoryMock;
import no.nav.bidrag.aktoerregister.repository.HendelseRepositoryMock;
import no.nav.bidrag.aktoerregister.repository.MockDB;
import no.nav.bidrag.felles.test.data.konto.TestKonto;
import no.nav.bidrag.felles.test.data.person.TestPerson;
import no.nav.bidrag.felles.test.data.samhandler.TestSamhandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;

@ExtendWith(MockitoExtension.class)
public class AktoerregisterServiceTest {
  private static final TestPerson PERSON1 = person().opprett();
  private static final TestKonto KONTO1 = konto().opprett();
  private static final TestSamhandler SAMHANDLER1 = samhandler().opprett();
  private static final TestSamhandler SAMHANDLER2 = samhandler().opprett();
  private final AktoerTilAktoerDTOConverter aktoerTilAktoerDTOConverter =
      new AktoerTilAktoerDTOConverter();
  private MockDB mockDB;
  @Mock private AktoerService tpsService;
  @Mock private AktoerService tssService;
  @Mock private ConversionService conversionService;
  private AktoerregisterService aktoerregisterService;

  @BeforeEach
  public void SetUp() {
    mockDB = new MockDB();
    AktoerRepositoryMock aktoerRepository = new AktoerRepositoryMock(mockDB);
    HendelseRepositoryMock hendelseRepositoryMock = new HendelseRepositoryMock(mockDB);
    aktoerregisterService =
        new AktoerregisterServiceImpl(
            aktoerRepository, hendelseRepositoryMock, tpsService, tssService, conversionService);
  }

  @Test
  public void skalHenteAktoerMedPersonnummerOgAktoerIkkeFinnes() {
    Aktoer aktoer =
        opprettTPSAktoerDTOMedNorskKontonr(PERSON1.getPersonIdent(), KONTO1.getNorskKontonummer());

    when(tpsService.hentAktoer(any())).thenReturn(aktoer);
    when(conversionService.convert(any(Aktoer.class), eq(AktoerDTO.class)))
        .thenReturn(aktoerTilAktoerDTOConverter.convert(aktoer));

    AktoerDTO aktoerFromTPS =
        aktoerregisterService.hentAktoer(
            AktoerIdDTO.builder()
                .aktoerId(aktoer.getAktoerIdent())
                .identtype(IdenttypeDTO.valueOf(aktoer.getAktoerType()))
                .build());

    verify(tpsService, times(1)).hentAktoer(aktoer.getAktoerIdent());

    assertNotNull(aktoerFromTPS);
    assertEquals(aktoerFromTPS.getKontonummer().getNorskKontonr(), aktoer.getNorskKontonr());

    assertEquals(mockDB.aktoerMap.size(), 1);
    assertEquals(mockDB.hendelseMap.size(), 1);
  }

  @Test
  public void skalHenteAktoerMedAktoernummerOgAktoerIkkeFinnes() {
    Aktoer aktoer = opprettTSSAktoerDTO(SAMHANDLER1);
    AktoerIdDTO aktoerIdDTO =
        AktoerIdDTO.builder()
            .aktoerId(aktoer.getAktoerIdent())
            .identtype(IdenttypeDTO.valueOf(aktoer.getAktoerType()))
            .build();

    when(tssService.hentAktoer(any())).thenReturn(aktoer);
    when(conversionService.convert(any(Aktoer.class), eq(AktoerDTO.class)))
        .thenReturn(aktoerTilAktoerDTOConverter.convert(aktoer));

    AktoerDTO aktoerFromTSS = aktoerregisterService.hentAktoer(aktoerIdDTO);

    verify(tssService, times(1)).hentAktoer(aktoer.getAktoerIdent());

    assertNotNull(aktoerFromTSS);
    assertEquals(aktoerFromTSS.getKontonummer().getNorskKontonr(), aktoer.getNorskKontonr());
    assertEquals(aktoerFromTSS.getAdresse().getAdresselinje1(), aktoer.getAdresselinje1());

    assertEquals(1, mockDB.aktoerMap.size());
    assertEquals(1, mockDB.hendelseMap.size());
  }

  @Test
  public void skalOppdatereAktoer() {
    Aktoer aktoer = opprettTSSAktoerDTO(SAMHANDLER1);
    AktoerIdDTO aktoerIdDTO =
        AktoerIdDTO.builder()
            .aktoerId(aktoer.getAktoerIdent())
            .identtype(IdenttypeDTO.valueOf(aktoer.getAktoerType()))
            .build();

    when(tssService.hentAktoer(any())).thenReturn(aktoer);
    when(conversionService.convert(any(Aktoer.class), eq(AktoerDTO.class)))
        .thenReturn(aktoerTilAktoerDTOConverter.convert(aktoer));

    aktoerregisterService.hentAktoer(aktoerIdDTO);

    aktoer.setAdresselinje1("Testgate 2");

    aktoerregisterService.oppdaterAktoer(aktoer);

    when(conversionService.convert(any(Aktoer.class), eq(AktoerDTO.class)))
        .thenReturn(aktoerTilAktoerDTOConverter.convert(aktoer));

    AktoerDTO aktoerFromDb = aktoerregisterService.hentAktoer(aktoerIdDTO);

    verify(tssService, times(1)).hentAktoer(aktoer.getAktoerIdent());

    assertEquals("Testgate 2", aktoerFromDb.getAdresse().getAdresselinje1());

    assertEquals(1, mockDB.aktoerMap.size());
    assertEquals(2, mockDB.hendelseMap.size());

    assertEquals(
        SAMHANDLER1.getSamhandlerIdent(), mockDB.hendelseMap.get(1).getAktoer().getAktoerIdent());
    assertEquals(
        SAMHANDLER1.getSamhandlerIdent(), mockDB.hendelseMap.get(2).getAktoer().getAktoerIdent());
  }

  @Test
  public void TestHentHendelser() {
    Aktoer aktoer = opprettTSSAktoerDTO(SAMHANDLER1);
    AktoerIdDTO aktoerIdDTO =
        AktoerIdDTO.builder()
            .aktoerId(aktoer.getAktoerIdent())
            .identtype(IdenttypeDTO.valueOf(aktoer.getAktoerType()))
            .build();

    when(tssService.hentAktoer(any())).thenReturn(aktoer);
    when(conversionService.convert(any(Aktoer.class), eq(AktoerDTO.class)))
        .thenReturn(aktoerTilAktoerDTOConverter.convert(aktoer));

    aktoerregisterService.hentAktoer(aktoerIdDTO);

    aktoer.setAdresselinje1("Testgate 2");
    aktoerregisterService.oppdaterAktoer(aktoer);

    aktoer.setAdresselinje1("Testgate 3");
    aktoerregisterService.oppdaterAktoer(aktoer);

    aktoer.setAdresselinje1("Testgate 4");
    aktoerregisterService.oppdaterAktoer(aktoer);

    List<HendelseDTO> hendelseDTOList = aktoerregisterService.hentHendelser(0, 10);

    assertEquals(1, hendelseDTOList.size());
    assertEquals(4, hendelseDTOList.get(0).getSekvensnummer());

    Aktoer aktoer2 = opprettTSSAktoerDTO(SAMHANDLER2);
    AktoerIdDTO aktoerIdDTO2 =
        AktoerIdDTO.builder()
            .aktoerId(aktoer2.getAktoerIdent())
            .identtype(IdenttypeDTO.valueOf(aktoer2.getAktoerType()))
            .build();

    when(tssService.hentAktoer(any())).thenReturn(aktoer2);
    aktoerregisterService.hentAktoer(aktoerIdDTO2);

    aktoer2.setAdresselinje1("Testgate 2");
    aktoerregisterService.oppdaterAktoer(aktoer2);

    aktoer2.setAdresselinje1("Testgate 3");
    aktoerregisterService.oppdaterAktoer(aktoer2);

    aktoer2.setAdresselinje1("Testgate 4");
    aktoerregisterService.oppdaterAktoer(aktoer2);

    hendelseDTOList = aktoerregisterService.hentHendelser(0, 10);

    assertEquals(2, hendelseDTOList.size());
    assertEquals(4, hendelseDTOList.get(0).getSekvensnummer());
    assertEquals(8, hendelseDTOList.get(1).getSekvensnummer());
  }

  private Aktoer opprettTPSAktoerDTOMedNorskKontonr(String fnr, String kontonummer) {
    Aktoer aktoer = new Aktoer();
    aktoer.setAktoerIdent(fnr);
    aktoer.setAktoerType(IdenttypeDTO.PERSONNUMMER.name());
    aktoer.setNorskKontonr(kontonummer);
    return aktoer;
  }

  private Aktoer opprettTSSAktoerDTO(TestSamhandler samhandler) {
    return Aktoer.builder()
        .aktoerIdent(samhandler.getSamhandlerIdent())
        .aktoerType(IdenttypeDTO.AKTOERNUMMER.name())
        .norskKontonr(AktoerregisterServiceTest.KONTO1.getNorskKontonummer())
        .adresselinje1("Testgate 1")
        .build();
  }
}

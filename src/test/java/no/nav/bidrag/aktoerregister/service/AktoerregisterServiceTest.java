package no.nav.bidrag.aktoerregister.service;

import static no.nav.bidrag.felles.test.data.konto.TestKontoBuilder.konto;
import static no.nav.bidrag.felles.test.data.person.TestPersonBuilder.person;
import static no.nav.bidrag.felles.test.data.samhandler.TestSamhandlerBuilder.samhandler;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import no.nav.bidrag.aktoerregister.domene.AktoerDTO;
import no.nav.bidrag.aktoerregister.domene.AktoerIdDTO;
import no.nav.bidrag.aktoerregister.domene.HendelseDTO;
import no.nav.bidrag.aktoerregister.domene.IdenttypeDTO;
import no.nav.bidrag.aktoerregister.domene.KontonummerDTO;
import no.nav.bidrag.aktoerregister.exception.AktoerNotFoundException;
import no.nav.bidrag.aktoerregister.exception.MQServiceException;
import no.nav.bidrag.aktoerregister.exception.TPSServiceException;
import no.nav.bidrag.aktoerregister.exception.TSSServiceException;
import no.nav.bidrag.aktoerregister.mapper.AktoerMapper;
import no.nav.bidrag.aktoerregister.mapper.Mapper;
import no.nav.bidrag.aktoerregister.persistence.entities.Adresse;
import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;
import no.nav.bidrag.aktoerregister.persistence.entities.Kontonummer;
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

@ExtendWith(MockitoExtension.class)
public class AktoerregisterServiceTest {
  private static final TestPerson PERSON1 = person().opprett();
  private static final TestKonto KONTO1 = konto().opprett();
  private static final TestSamhandler SAMHANDLER1 = samhandler().opprett();
  private static final TestSamhandler SAMHANDLER2 = samhandler().opprett();

  private MockDB mockDB;

  @Mock private TPSService tpsService;

  @Mock private TSSService tssService;

  private AktoerregisterService aktoerregisterService;

  private Mapper<AktoerDTO, Aktoer> aktoerMapper;

  @BeforeEach
  public void SetUp() {
    mockDB = new MockDB();
    AktoerRepositoryMock aktoerRepository = new AktoerRepositoryMock(mockDB);
    HendelseRepositoryMock hendelseRepositoryMock = new HendelseRepositoryMock(mockDB);
    aktoerregisterService =
        new AktoerregisterServiceImpl(
            aktoerRepository, hendelseRepositoryMock, tpsService, tssService);
    aktoerMapper = new AktoerMapper();
  }

  @Test
  public void TestHentAktoerWithPersonnummerAndAktoerDoesNotExist()
      throws MQServiceException, AktoerNotFoundException, TPSServiceException, TSSServiceException {
    AktoerDTO aktoerDTO =
        createTPSAktoerDTO(PERSON1.getPersonIdent(), KONTO1.getNorskKontonummer(), true);

    when(tpsService.hentAktoer(any())).thenReturn(aktoerDTO);

    AktoerDTO aktoerFromTPS = aktoerregisterService.hentAktoer(aktoerDTO.getAktoerId());

    verify(tpsService, times(1)).hentAktoer(aktoerDTO.getAktoerId());

    assertNotNull(aktoerFromTPS);
    assertEquals(
        aktoerFromTPS.getKontonummer().getNorskKontonr(),
        aktoerDTO.getKontonummer().getNorskKontonr());

    assertEquals(mockDB.aktoerMap.size(), 1);
    assertEquals(mockDB.hendelseMap.size(), 1);
  }

  @Test
  public void TestHentAktoerWithAktoernummerAndAktoerDoesNotExist()
      throws MQServiceException, TSSServiceException, AktoerNotFoundException, TPSServiceException {
    Aktoer aktoer = createTSSAktoerDTO(SAMHANDLER1, KONTO1, "Testgate 1", true);
    AktoerIdDTO aktoerIdDTO =
        new AktoerIdDTO(aktoer.getAktoerIdent(), IdenttypeDTO.valueOf(aktoer.getAktoerType()));

    when(tssService.hentAktoer(any())).thenReturn(aktoerMapper.toDomain(aktoer));

    AktoerDTO aktoerFromTSS = aktoerregisterService.hentAktoer(aktoerIdDTO);

    verify(tssService, times(1)).hentAktoer(aktoerIdDTO);

    assertNotNull(aktoerFromTSS);
    assertEquals(
        aktoerFromTSS.getKontonummer().getNorskKontonr(),
        aktoer.getKontonummer().getNorskKontonr());
    assertEquals(
        aktoerFromTSS.getAdresse().getAdresselinje1(), aktoer.getAdresse().getAdresselinje1());

    assertEquals(1, mockDB.aktoerMap.size());
    assertEquals(1, mockDB.hendelseMap.size());
  }

  @Test
  public void TestOppdaterAktoer()
      throws MQServiceException, TSSServiceException, AktoerNotFoundException, TPSServiceException {
    Aktoer aktoer = createTSSAktoerDTO(SAMHANDLER1, KONTO1, "Testgate 1", true);
    AktoerIdDTO aktoerIdDTO =
        new AktoerIdDTO(aktoer.getAktoerIdent(), IdenttypeDTO.valueOf(aktoer.getAktoerType()));

    when(tssService.hentAktoer(any())).thenReturn(aktoerMapper.toDomain(aktoer));

    aktoerregisterService.hentAktoer(aktoerIdDTO);

    aktoer.getAdresse().setAdresselinje1("Testgate 2");

    aktoerregisterService.oppdaterAktoer(aktoer);

    AktoerDTO aktoerFromDb = aktoerregisterService.hentAktoer(aktoerIdDTO);

    verify(tssService, times(1)).hentAktoer(aktoerIdDTO);

    assertEquals("Testgate 2", aktoerFromDb.getAdresse().getAdresselinje1());

    assertEquals(1, mockDB.aktoerMap.size());
    assertEquals(2, mockDB.hendelseMap.size());

    assertEquals(
        SAMHANDLER1.getSamhandlerIdent(), mockDB.hendelseMap.get(1).getAktoer().getAktoerIdent());
    assertEquals(
        SAMHANDLER1.getSamhandlerIdent(), mockDB.hendelseMap.get(2).getAktoer().getAktoerIdent());
  }

  @Test
  public void TestHentHendelser()
      throws MQServiceException, TSSServiceException, AktoerNotFoundException, TPSServiceException {
    Aktoer aktoer = createTSSAktoerDTO(SAMHANDLER1, KONTO1, "Testgate 1", true);
    AktoerIdDTO aktoerIdDTO =
        new AktoerIdDTO(aktoer.getAktoerIdent(), IdenttypeDTO.valueOf(aktoer.getAktoerType()));

    when(tssService.hentAktoer(any())).thenReturn(aktoerMapper.toDomain(aktoer));
    aktoerregisterService.hentAktoer(aktoerIdDTO);

    aktoer.getAdresse().setAdresselinje1("Testgate 2");
    aktoerregisterService.oppdaterAktoer(aktoer);

    aktoer.getAdresse().setAdresselinje1("Testgate 3");
    aktoerregisterService.oppdaterAktoer(aktoer);

    aktoer.getAdresse().setAdresselinje1("Testgate 4");
    aktoerregisterService.oppdaterAktoer(aktoer);

    List<HendelseDTO> hendelseDTOList = aktoerregisterService.hentHendelser(0, 10);

    assertEquals(1, hendelseDTOList.size());
    assertEquals(4, hendelseDTOList.get(0).getSekvensnummer());

    Aktoer aktoer2 = createTSSAktoerDTO(SAMHANDLER2, KONTO1, "Testgate 1", true);
    AktoerIdDTO aktoerIdDTO2 =
        new AktoerIdDTO(aktoer2.getAktoerIdent(), IdenttypeDTO.valueOf(aktoer2.getAktoerType()));

    when(tssService.hentAktoer(any())).thenReturn(aktoerMapper.toDomain(aktoer2));
    aktoerregisterService.hentAktoer(aktoerIdDTO2);

    aktoer2.getAdresse().setAdresselinje1("Testgate 2");
    aktoerregisterService.oppdaterAktoer(aktoer2);

    aktoer2.getAdresse().setAdresselinje1("Testgate 3");
    aktoerregisterService.oppdaterAktoer(aktoer2);

    aktoer2.getAdresse().setAdresselinje1("Testgate 4");
    aktoerregisterService.oppdaterAktoer(aktoer2);

    hendelseDTOList = aktoerregisterService.hentHendelser(0, 10);

    assertEquals(2, hendelseDTOList.size());
    assertEquals(4, hendelseDTOList.get(0).getSekvensnummer());
    assertEquals(8, hendelseDTOList.get(1).getSekvensnummer());
  }

  private AktoerDTO createTPSAktoerDTO(String fnr, String kontonummer, boolean norsk) {
    AktoerDTO aktoerDTO = new AktoerDTO();
    AktoerIdDTO aktoerIdDTO = new AktoerIdDTO();
    aktoerIdDTO.setAktoerId(fnr);
    aktoerIdDTO.setIdenttype(IdenttypeDTO.PERSONNUMMER);
    KontonummerDTO kontonummerDTO = new KontonummerDTO();
    if (norsk) {
      kontonummerDTO.setNorskKontonr(kontonummer);
    } else {
      kontonummerDTO.setIban(kontonummer);
    }
    aktoerDTO.setAktoerId(aktoerIdDTO);
    aktoerDTO.setKontonummer(kontonummerDTO);
    return aktoerDTO;
  }

  private Aktoer createTSSAktoerDTO(
      TestSamhandler samhandler, TestKonto konto, String adresselinje1, boolean norsk) {
    Aktoer aktoer = new Aktoer();
    AktoerIdDTO aktoerIdDTO = new AktoerIdDTO();
    aktoerIdDTO.setAktoerId(samhandler.getSamhandlerIdent());
    aktoerIdDTO.setIdenttype(IdenttypeDTO.AKTOERNUMMER);
    Kontonummer kontonummerDTO = new Kontonummer();
    if (norsk) {
      kontonummerDTO.setNorskKontonr(konto.getNorskKontonummer());
    } else {
      kontonummerDTO.setIban(konto.getNorskKontonummer());
    }
    Adresse adresseDTO = new Adresse();
    adresseDTO.setAdresselinje1(adresselinje1);

    aktoer.setAktoerIdent(samhandler.getSamhandlerIdent());
    aktoer.setAktoerType(IdenttypeDTO.AKTOERNUMMER.name());
    aktoer.setAdresse(adresseDTO);
    aktoer.setKontonummer(kontonummerDTO);
    return aktoer;
  }
}

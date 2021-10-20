package no.nav.bidrag.aktoerregister.service;

import java.util.List;
import no.nav.bidrag.aktoerregister.domene.AdresseDTO;
import no.nav.bidrag.aktoerregister.domene.AktoerDTO;
import no.nav.bidrag.aktoerregister.domene.AktoerIdDTO;
import no.nav.bidrag.aktoerregister.domene.HendelseDTO;
import no.nav.bidrag.aktoerregister.domene.IdenttypeDTO;
import no.nav.bidrag.aktoerregister.domene.KontonummerDTO;
import no.nav.bidrag.aktoerregister.exception.AktoerNotFoundException;
import no.nav.bidrag.aktoerregister.exception.MQServiceException;
import no.nav.bidrag.aktoerregister.exception.TPSServiceException;
import no.nav.bidrag.aktoerregister.exception.TSSServiceException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import no.nav.bidrag.aktoerregister.mapper.AktoerMapper;
import no.nav.bidrag.aktoerregister.mapper.Mapper;
import no.nav.bidrag.aktoerregister.persistence.entities.Adresse;
import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;
import no.nav.bidrag.aktoerregister.persistence.entities.Kontonummer;
import no.nav.bidrag.aktoerregister.repository.AktoerRepositoryMock;
import no.nav.bidrag.aktoerregister.repository.HendelseRepositoryMock;
import no.nav.bidrag.aktoerregister.repository.MockDB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AktoerregisterServiceTest {

  private AktoerRepositoryMock aktoerRepository;

  private HendelseRepositoryMock hendelseRepositoryMock;

  private MockDB mockDB;

  @Mock
  private TPSService tpsService;

  @Mock
  private TSSService tssService;

  private AktoerregisterService aktoerregisterService;

  private Mapper<AktoerDTO, Aktoer> aktoerMapper;

  @BeforeEach
  public void SetUp() {
    mockDB = new MockDB();
    aktoerRepository = new AktoerRepositoryMock(mockDB);
    hendelseRepositoryMock = new HendelseRepositoryMock(mockDB);
    aktoerregisterService = new AktoerregisterServiceImpl(aktoerRepository, hendelseRepositoryMock, tpsService, tssService);
    aktoerMapper = new AktoerMapper();
  }

  @Test
  public void TestHentAktoerWithPersonnummerAndAktoerDoesNotExist()
      throws MQServiceException, AktoerNotFoundException, TPSServiceException, TSSServiceException {
    AktoerDTO aktoerDTO = createTPSAktoerDTO("12345678901", "242526272829", true);

    when(tpsService.hentAktoer(any())).thenReturn(aktoerDTO);

    AktoerDTO aktoerFromTPS = aktoerregisterService.hentAktoer(aktoerDTO.getAktoerId());

    verify(tpsService, times(1)).hentAktoer(aktoerDTO.getAktoerId());

    assertNotNull(aktoerFromTPS);
    assertEquals(aktoerFromTPS.getKontonummer().getNorskKontonr(), aktoerDTO.getKontonummer().getNorskKontonr());

    assertEquals(mockDB.aktoerMap.size(), 1);
    assertEquals(mockDB.hendelseMap.size(), 1);
  }

  @Test
  public void TestHentAktoerWithAktoernummerAndAktoerDoesNotExist()
      throws MQServiceException, TSSServiceException, AktoerNotFoundException, TPSServiceException {
    Aktoer aktoer = createTSSAktoerDTO("12345678901", "242526272829", "Testgate 1", true);
    AktoerIdDTO aktoerIdDTO = new AktoerIdDTO(aktoer.getAktoerId(), IdenttypeDTO.valueOf(aktoer.getAktoerType()));

    when(tssService.hentAktoer(any())).thenReturn(aktoerMapper.toDomain(aktoer));

    AktoerDTO aktoerFromTSS = aktoerregisterService.hentAktoer(aktoerIdDTO);

    verify(tssService, times(1)).hentAktoer(aktoerIdDTO);

    assertNotNull(aktoerFromTSS);
    assertEquals(aktoerFromTSS.getKontonummer().getNorskKontonr(), aktoer.getKontonummer().getNorskKontonr());
    assertEquals(aktoerFromTSS.getAdresse().getAdresselinje1(), aktoer.getAdresse().getAdresselinje1());

    assertEquals(1, mockDB.aktoerMap.size());
    assertEquals(1, mockDB.hendelseMap.size());
  }

  @Test
  public void TestOppdaterAktoer() throws MQServiceException, TSSServiceException, AktoerNotFoundException, TPSServiceException {
    Aktoer aktoer = createTSSAktoerDTO("12345678901", "242526272829", "Testgate 1", true);
    AktoerIdDTO aktoerIdDTO = new AktoerIdDTO(aktoer.getAktoerId(), IdenttypeDTO.valueOf(aktoer.getAktoerType()));

    when(tssService.hentAktoer(any())).thenReturn(aktoerMapper.toDomain(aktoer));

    aktoerregisterService.hentAktoer(aktoerIdDTO);

    aktoer.getAdresse().setAdresselinje1("Testgate 2");

    aktoerregisterService.oppdaterAktoer(aktoer);

    AktoerDTO aktoerFromDb = aktoerregisterService.hentAktoer(aktoerIdDTO);

    verify(tssService, times(1)).hentAktoer(aktoerIdDTO);

    assertEquals("Testgate 2", aktoerFromDb.getAdresse().getAdresselinje1());

    assertEquals(1, mockDB.aktoerMap.size());
    assertEquals(2, mockDB.hendelseMap.size());

    assertEquals("12345678901", mockDB.hendelseMap.get(1).getAktoer().getAktoerId());
    assertEquals("12345678901", mockDB.hendelseMap.get(2).getAktoer().getAktoerId());
  }

  @Test
  public void TestHentHendelser() throws MQServiceException, TSSServiceException, AktoerNotFoundException, TPSServiceException {
    Aktoer aktoer = createTSSAktoerDTO("12345678901", "242526272829", "Testgate 1", true);
    AktoerIdDTO aktoerIdDTO = new AktoerIdDTO(aktoer.getAktoerId(), IdenttypeDTO.valueOf(aktoer.getAktoerType()));

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

    Aktoer aktoer2 = createTSSAktoerDTO("12345678902", "242526272823", "Testgate 1", true);
    AktoerIdDTO aktoerIdDTO2 = new AktoerIdDTO(aktoer2.getAktoerId(), IdenttypeDTO.valueOf(aktoer2.getAktoerType()));

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

  private Aktoer createTSSAktoerDTO(String aktoerId, String kontonummer, String adresselinje1, boolean norsk) {
    Aktoer aktoer = new Aktoer();
    AktoerIdDTO aktoerIdDTO = new AktoerIdDTO();
    aktoerIdDTO.setAktoerId(aktoerId);
    aktoerIdDTO.setIdenttype(IdenttypeDTO.AKTOERNUMMER);
    Kontonummer kontonummerDTO = new Kontonummer();
    if (norsk) {
      kontonummerDTO.setNorskKontonr(kontonummer);
    } else {
      kontonummerDTO.setIban(kontonummer);
    }
    Adresse adresseDTO = new Adresse();
    adresseDTO.setAdresselinje1(adresselinje1);

    aktoer.setAktoerId(aktoerId);
    aktoer.setAktoerType(IdenttypeDTO.AKTOERNUMMER.name());
    aktoer.setAdresse(adresseDTO);
    aktoer.setKontonummer(kontonummerDTO);
    return aktoer;
  }

}

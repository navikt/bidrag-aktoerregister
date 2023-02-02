package no.nav.bidrag.aktoerregister.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import no.nav.bidrag.aktoerregister.domene.AdresseDTO;
import no.nav.bidrag.aktoerregister.domene.AktoerDTO;
import no.nav.bidrag.aktoerregister.domene.AktoerIdDTO;
import no.nav.bidrag.aktoerregister.domene.IdenttypeDTO;
import no.nav.bidrag.aktoerregister.domene.KontonummerDTO;
import no.nav.bidrag.aktoerregister.mapper.AktoerMapper;
import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;
import org.junit.jupiter.api.Test;

public class MapperTests {

  @Test
  public void TestAktoerMapper() {
    AktoerIdDTO aktoerIdDTO = new AktoerIdDTO();
    aktoerIdDTO.setAktoerId("17818798717");
    aktoerIdDTO.setIdenttype(IdenttypeDTO.PERSONNUMMER);
    AktoerDTO aktoerDTO = new AktoerDTO(aktoerIdDTO);

    AdresseDTO adresseDTO = new AdresseDTO();
    adresseDTO.setAdresselinje1("Testgate 1");
    adresseDTO.setLand("Norge");
    adresseDTO.setPostnr("0682");

    KontonummerDTO kontonummerDTO = new KontonummerDTO();
    kontonummerDTO.setNorskKontonr("62081012345");

    aktoerDTO.setAdresse(adresseDTO);

    aktoerDTO.setKontonummer(kontonummerDTO);

    AktoerMapper aktoerMapper = new AktoerMapper();
    Aktoer aktoer = aktoerMapper.toPersistence(aktoerDTO);

    AktoerDTO mappedAktoerDTO = aktoerMapper.toDomain(aktoer);

    assertNotNull(aktoer);
    assertEquals(aktoer.getAktoerIdent(), aktoerDTO.getAktoerId().getAktoerId());
    assertEquals(aktoer.getAktoerType(), aktoerDTO.getAktoerId().getIdenttype().name());

    assertEquals(aktoer.getAdresse().getAdresselinje1(), aktoerDTO.getAdresse().getAdresselinje1());
    assertEquals(aktoer.getAdresse().getLand(), aktoerDTO.getAdresse().getLand());
    assertEquals(aktoer.getAdresse().getPostnr(), aktoerDTO.getAdresse().getPostnr());

    assertEquals(
        aktoer.getKontonummer().getNorskKontonr(), aktoerDTO.getKontonummer().getNorskKontonr());

    assertNotNull(mappedAktoerDTO);
    assertEquals(mappedAktoerDTO.getAktoerId().getAktoerId(), aktoer.getAktoerIdent());
    assertEquals(mappedAktoerDTO.getAktoerId().getIdenttype().name(), aktoer.getAktoerType());

    assertEquals(
        mappedAktoerDTO.getAdresse().getAdresselinje1(), aktoer.getAdresse().getAdresselinje1());
    assertEquals(mappedAktoerDTO.getAdresse().getLand(), aktoer.getAdresse().getLand());
    assertEquals(mappedAktoerDTO.getAdresse().getPostnr(), aktoer.getAdresse().getPostnr());

    assertEquals(
        mappedAktoerDTO.getKontonummer().getNorskKontonr(),
        aktoer.getKontonummer().getNorskKontonr());
  }
}

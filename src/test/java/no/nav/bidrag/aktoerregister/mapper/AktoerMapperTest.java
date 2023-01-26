package no.nav.bidrag.aktoerregister.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import no.nav.bidrag.aktoerregister.domene.AdresseDTO;
import no.nav.bidrag.aktoerregister.domene.AktoerDTO;
import no.nav.bidrag.aktoerregister.domene.AktoerIdDTO;
import no.nav.bidrag.aktoerregister.domene.IdenttypeDTO;
import no.nav.bidrag.aktoerregister.domene.KontonummerDTO;
import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;
import org.junit.jupiter.api.Test;

class AktoerMapperTest {

  private final AktoerMapper aktoerMapper = new AktoerMapper();

  @Test
  void skalMappeFraDtoTilDomene() {
    AktoerDTO aktoerDTO = new AktoerDTO(new AktoerIdDTO("TestIdent123", IdenttypeDTO.PERSONNUMMER));
    aktoerDTO.setOffentligId("TESTID");
    aktoerDTO.setOffentligIdType("UTOR");
    AdresseDTO adresseDTO = new AdresseDTO();
    adresseDTO.setAdresselinje1("Linje1");
    adresseDTO.setAdresselinje2("Linje2");
    adresseDTO.setNavn("AdresseNavn");
    adresseDTO.setLand("TestLand");
    aktoerDTO.setAdresse(adresseDTO);
    KontonummerDTO kontonummer = new KontonummerDTO();
    kontonummer.setIban("IBANTEST");
    kontonummer.setSwift("SWIFTTEST");
    kontonummer.setBankNavn("TEST Bank");
    kontonummer.setBankLandkode("NOR");
    kontonummer.setValutaKode("NOK");
    aktoerDTO.setKontonummer(kontonummer);

    Aktoer aktoer = aktoerMapper.toPersistence(aktoerDTO);

    assertThat(aktoer.getAktoerIdent()).isEqualTo(aktoerDTO.getAktoerId().getAktoerId());
  }

  @Test
  void skalMappeFraFomeneTilDto() {
    Aktoer aktoer = new Aktoer();
    aktoer.setAktoerIdent("TestIdent321");
    aktoer.setAktoerType("PERSONNUMMER");

    AktoerDTO aktoerDTO = aktoerMapper.toDomain(aktoer);

    assertThat(aktoerDTO.getAktoerId().getAktoerId()).isEqualTo(aktoer.getAktoerIdent());
  }
}

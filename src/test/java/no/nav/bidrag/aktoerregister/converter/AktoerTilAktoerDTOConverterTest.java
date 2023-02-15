package no.nav.bidrag.aktoerregister.converter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import no.nav.bidrag.aktoerregister.domene.AktoerDTO;
import no.nav.bidrag.aktoerregister.domene.enumer.IdenttypeDTO;
import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;
import no.nav.bidrag.commons.util.PersonidentGenerator;
import org.junit.jupiter.api.Test;

class AktoerTilAktoerDTOConverterTest {

  private final AktoerTilAktoerDTOConverter aktoerTilAktoerDTOConverter =
      new AktoerTilAktoerDTOConverter();

  @Test
  void skalKonvertereAktoerTilAktoerDTO() {
    String aktoerIdent = PersonidentGenerator.INSTANCE.genererPersonnummer(null, null);
    IdenttypeDTO aktoerType = IdenttypeDTO.PERSONNUMMER;
    String offentligId = "6";
    String offentligType = "OffentligType";
    String norskKontonr = "123456789";
    String iban = "12345";
    String swift = "54321";
    String bankNavn = "Bank of Navn";
    String bankCode = "1899";
    String bankLandkode = "NOR";
    String valutaKode = "NOK";
    String navn = "Testnavn";
    String adresselinje1 = "Test gate 10";
    String adresselinje2 = "Test gate 20";
    String adresselinje3 = "Test gate 30";
    String postnr = "0001";
    String poststed = "Oslo";

    Aktoer aktoer =
        Aktoer.builder()
            .aktoerIdent(aktoerIdent)
            .aktoerType(aktoerType.name())
            .offentligId(offentligId)
            .offentligIdType(offentligType)
            .norskKontonr(norskKontonr)
            .iban(iban)
            .swift(swift)
            .bankNavn(bankNavn)
            .bankCode(bankCode)
            .bankLandkode(bankLandkode)
            .valutaKode(valutaKode)
            .navn(navn)
            .adresselinje1(adresselinje1)
            .adresselinje2(adresselinje2)
            .adresselinje3(adresselinje3)
            .postnr(postnr)
            .poststed(poststed)
            .build();

    AktoerDTO aktoerDTO = aktoerTilAktoerDTOConverter.convert(aktoer);

    assertThat(aktoerDTO).isNotNull();
    assertThat(aktoerDTO.getAktoerId()).isNotNull();
    assertThat(aktoerDTO.getAktoerId().getAktoerId()).isEqualTo(aktoerIdent);
    assertThat(aktoerDTO.getAktoerId().getIdenttype()).isEqualTo(aktoerType);
    assertThat(aktoerDTO.getOffentligId()).isEqualTo(offentligId);
    assertThat(aktoerDTO.getOffentligIdType()).isEqualTo(offentligType);
    assertThat(aktoerDTO.getKontonummer().getNorskKontonr()).isEqualTo(norskKontonr);
    assertThat(aktoerDTO.getKontonummer().getIban()).isEqualTo(iban);
    assertThat(aktoerDTO.getKontonummer().getSwift()).isEqualTo(swift);
    assertThat(aktoerDTO.getKontonummer().getBankNavn()).isEqualTo(bankNavn);
    assertThat(aktoerDTO.getKontonummer().getBankCode()).isEqualTo(bankCode);
    assertThat(aktoerDTO.getKontonummer().getBankLandkode()).isEqualTo(bankLandkode);
    assertThat(aktoerDTO.getKontonummer().getValutaKode()).isEqualTo(valutaKode);
    assertThat(aktoerDTO.getAdresse().getNavn()).isEqualTo(navn);
    assertThat(aktoerDTO.getAdresse().getAdresselinje1()).isEqualTo(adresselinje1);
    assertThat(aktoerDTO.getAdresse().getAdresselinje2()).isEqualTo(adresselinje2);
    assertThat(aktoerDTO.getAdresse().getAdresselinje3()).isEqualTo(adresselinje3);
    assertThat(aktoerDTO.getAdresse().getPostnr()).isEqualTo(postnr);
    assertThat(aktoerDTO.getAdresse().getPoststed()).isEqualTo(poststed);
  }
}

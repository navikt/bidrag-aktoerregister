package no.nav.bidrag.aktoerregister.converter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDate;
import java.util.List;
import no.nav.bidrag.aktoerregister.dto.AktoerDTO;
import no.nav.bidrag.aktoerregister.dto.enumer.Gradering;
import no.nav.bidrag.aktoerregister.dto.enumer.Identtype;
import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;
import no.nav.bidrag.aktoerregister.persistence.entities.Dodsbo;
import no.nav.bidrag.aktoerregister.persistence.entities.TidligereIdenter;
import no.nav.bidrag.commons.util.PersonidentGenerator;
import org.junit.jupiter.api.Test;

class AktoerTilAktoerDTOConverterTest {

  private final AktoerTilAktoerDTOConverter aktoerTilAktoerDTOConverter =
      new AktoerTilAktoerDTOConverter();

  @Test
  void skalKonverteSelvOmAlleFelterErNull() {
    Aktoer aktoer = Aktoer.builder().build();

    AktoerDTO aktoerDTO = aktoerTilAktoerDTOConverter.convert(aktoer);

    assertThat(aktoerDTO).isNotNull();
  }

  @Test
  void skalKonvertereAktoerTilAktoerDTO() {
    String aktoerIdent = PersonidentGenerator.INSTANCE.genererPersonnummer(null, null);
    String tidligereAktoerIdent = PersonidentGenerator.INSTANCE.genererPersonnummer(null, null);
    Identtype aktoerType = Identtype.PERSONNUMMER;
    String offentligId = "6";
    String offentligType = "OffentligType";
    String norskKontonr = "123456789";
    String iban = "12345";
    String swift = "54321";
    String bankNavn = "Bank of Navn";
    String bankCode = "1899";
    String bankLandkode = "NOR";
    String valutaKode = "NOK";
    String etternavn = "Etternavn";
    String fornavn = "Fornavn";
    Gradering gradering = Gradering.FORTROLIG;
    String sprakkode = "NOR";
    LocalDate foddato = LocalDate.now().minusYears(30);
    LocalDate doddato = LocalDate.now();
    String kontaktperson = "Kontakt";
    String adresselinje1 = "Test gate 10";
    String adresselinje2 = "Test gate 20";
    String adresselinje3 = "Test gate 30";
    String leilighetsnummer = "100";
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
            .etternavn(etternavn)
            .fornavn(fornavn)
            .tidligereIdenter(List.of(TidligereIdenter.builder().tidligereAktoerIdent(tidligereAktoerIdent).identtype(aktoerType.name()).build()))
            .gradering(gradering.name())
            .sprakkode(sprakkode)
            .fodtDato(foddato)
            .dodDato(doddato)
            .dodsbo(Dodsbo.builder().kontaktperson(kontaktperson).adresselinje1(adresselinje1).build())
            .adresselinje1(adresselinje1)
            .adresselinje2(adresselinje2)
            .adresselinje3(adresselinje3)
            .leilighetsnummer(leilighetsnummer)
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
    assertThat(aktoerDTO.getAdresse().getNavn()).isEqualTo(etternavn);
    assertThat(aktoerDTO.getAdresse().getAdresselinje1()).isEqualTo(adresselinje1);
    assertThat(aktoerDTO.getAdresse().getAdresselinje2()).isEqualTo(adresselinje2);
    assertThat(aktoerDTO.getAdresse().getAdresselinje3()).isEqualTo(adresselinje3);
    assertThat(aktoerDTO.getAdresse().getLeilighetsnummer()).isEqualTo(leilighetsnummer);
    assertThat(aktoerDTO.getAdresse().getPostnr()).isEqualTo(postnr);
    assertThat(aktoerDTO.getAdresse().getPoststed()).isEqualTo(poststed);
    assertThat(aktoerDTO.getNavn().getEtternavn()).isEqualTo(etternavn);
    assertThat(aktoerDTO.getNavn().getFornavn()).isEqualTo(fornavn);
    assertThat(aktoerDTO.getSprakkode()).isEqualTo(sprakkode);
    assertThat(aktoerDTO.getGradering()).isEqualTo(gradering);
    assertThat(aktoerDTO.getFodtDato()).isEqualTo(foddato.toString());
    assertThat(aktoerDTO.getDodDato()).isEqualTo(doddato.toString());
    assertThat(aktoerDTO.getTidligereIdenter().get(0).getAktoerId()).isEqualTo(tidligereAktoerIdent);
    assertThat(aktoerDTO.getTidligereIdenter().get(0).getIdenttype()).isEqualTo(aktoerType);
    assertThat(aktoerDTO.getDodsbo().getKontaktpersion()).isEqualTo(kontaktperson);
    assertThat(aktoerDTO.getDodsbo().getAdresse().getAdresselinje1()).isEqualTo(adresselinje1);
  }
}

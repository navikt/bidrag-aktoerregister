package no.nav.bidrag.aktoerregister.converter;

import no.nav.bidrag.aktoerregister.domene.AdresseDTO;
import no.nav.bidrag.aktoerregister.domene.AktoerDTO;
import no.nav.bidrag.aktoerregister.domene.AktoerIdDTO;
import no.nav.bidrag.aktoerregister.domene.KontonummerDTO;
import no.nav.bidrag.aktoerregister.domene.enumer.IdenttypeDTO;
import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;
import org.springframework.core.convert.converter.Converter;

public class AktoerTilAktoerDTOConverter implements Converter<Aktoer, AktoerDTO> {

  @Override
  public AktoerDTO convert(Aktoer aktoer) {
    return AktoerDTO.builder()
        .aktoerId(
            AktoerIdDTO.builder()
                .aktoerId(aktoer.getAktoerIdent())
                .identtype(IdenttypeDTO.valueOf(aktoer.getAktoerType()))
                .build())
        .offentligId(aktoer.getOffentligId())
        .offentligIdType(aktoer.getOffentligIdType())
        .adresse(
            AdresseDTO.builder()
                .navn(aktoer.getNavn())
                .land(aktoer.getLand())
                .postnr(aktoer.getPostnr())
                .poststed(aktoer.getPoststed())
                .adresselinje1(aktoer.getAdresselinje1())
                .adresselinje2(aktoer.getAdresselinje2())
                .adresselinje3(aktoer.getAdresselinje3())
                .build())
        .kontonummer(
            KontonummerDTO.builder()
                .norskKontonr(aktoer.getNorskKontonr())
                .iban(aktoer.getIban())
                .swift(aktoer.getSwift())
                .bankNavn(aktoer.getBankNavn())
                .bankLandkode(aktoer.getBankLandkode())
                .bankCode(aktoer.getBankCode())
                .valutaKode(aktoer.getValutaKode())
                .build())
        .build();
  }
}

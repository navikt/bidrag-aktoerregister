package no.nav.bidrag.aktoerregister.converter;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import no.nav.bidrag.aktoerregister.dto.AdresseDTO;
import no.nav.bidrag.aktoerregister.dto.AktoerDTO;
import no.nav.bidrag.aktoerregister.dto.AktoerIdDTO;
import no.nav.bidrag.aktoerregister.dto.DodsboDTO;
import no.nav.bidrag.aktoerregister.dto.KontonummerDTO;
import no.nav.bidrag.aktoerregister.dto.NavnDTO;
import no.nav.bidrag.aktoerregister.dto.enumer.Gradering;
import no.nav.bidrag.aktoerregister.dto.enumer.Identtype;
import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;
import no.nav.bidrag.aktoerregister.persistence.entities.Dodsbo;
import no.nav.bidrag.aktoerregister.persistence.entities.TidligereIdenter;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AktoerTilAktoerDTOConverter implements Converter<Aktoer, AktoerDTO> {

  @Override
  public AktoerDTO convert(@NotNull Aktoer aktoer) {
    return AktoerDTO.builder()
        .aktoerId(convertAktoerId(aktoer))
        .offentligId(aktoer.getOffentligId())
        .offentligIdType(aktoer.getOffentligIdType())
        .navn(convertNavn(aktoer))
        .adresse(convertAdresse(aktoer))
        .gradering(getGradering(aktoer))
        .sprakkode(aktoer.getSprakkode())
        .tidligereIdenter(convertTidligereIdenter(aktoer))
        .fodtDato(Objects.toString(aktoer.getFodtDato(), null))
        .dodDato(Objects.toString(aktoer.getDodDato(), null))
        .dodsbo(convertDodsbo(aktoer))
        .kontonummer(convertKontonummer(aktoer))
        .build();
  }

  private AktoerIdDTO convertAktoerId(@NotNull Aktoer aktoer) {
    return AktoerIdDTO.builder()
        .aktoerId(aktoer.getAktoerIdent())
        .identtype(aktoer.getAktoerType() != null ? Identtype.valueOf(aktoer.getAktoerType()) : null)
        .build();
  }

  private NavnDTO convertNavn(Aktoer aktoer) {
    return NavnDTO.builder()
        .fornavn(aktoer.getFornavn())
        .etternavn(aktoer.getEtternavn())
        .build();
  }

  private Gradering getGradering(@NotNull Aktoer aktoer) {
    return aktoer.getGradering() != null ? Gradering.valueOf(aktoer.getGradering()) : null;
  }

  private AdresseDTO convertAdresse(Aktoer aktoer) {
    return AdresseDTO.builder()
        .navn(aktoer.getEtternavn())
        .adresselinje1(aktoer.getAdresselinje1())
        .adresselinje2(aktoer.getAdresselinje2())
        .adresselinje3(aktoer.getAdresselinje3())
        .leilighetsnummer(aktoer.getLeilighetsnummer())
        .postnr(aktoer.getPostnr())
        .poststed(aktoer.getPoststed())
        .land(aktoer.getLand())
        .build();
  }

  private List<AktoerIdDTO> convertTidligereIdenter(Aktoer aktoer) {
    List<TidligereIdenter> identer = aktoer.getTidligereIdenter();

    return identer != null ?
        identer.stream()
            .map(tidligereIdent ->
                AktoerIdDTO.builder()
                    .aktoerId(tidligereIdent.getTidligereAktoerIdent())
                    .identtype(aktoer.getAktoerType() != null ? Identtype.valueOf(tidligereIdent.getIdenttype()) : null)
                    .build())
            .collect(Collectors.toList())
        : Collections.emptyList();
  }

  private DodsboDTO convertDodsbo(Aktoer aktoer) {
    Dodsbo dodsbo = aktoer.getDodsbo();

    return dodsbo != null
        ? DodsboDTO.builder()
        .kontaktpersion(aktoer.getDodsbo().getKontaktperson())
        .adresse(AdresseDTO.builder()
            .adresselinje1(aktoer.getDodsbo().getAdresselinje1())
            .adresselinje2(aktoer.getDodsbo().getAdresselinje2())
            .adresselinje3(aktoer.getDodsbo().getAdresselinje3())
            .leilighetsnummer(aktoer.getDodsbo().getLeilighetsnummer())
            .postnr(aktoer.getDodsbo().getPostnr())
            .poststed(aktoer.getDodsbo().getPoststed())
            .land(aktoer.getDodsbo().getLand())
            .build())
        .build()
        : DodsboDTO.builder().build();
  }

  private KontonummerDTO convertKontonummer(Aktoer aktoer) {
    return KontonummerDTO.builder()
        .norskKontonr(aktoer.getNorskKontonr())
        .iban(aktoer.getIban())
        .swift(aktoer.getSwift())
        .bankCode(aktoer.getBankCode())
        .bankNavn(aktoer.getBankNavn())
        .bankLandkode(aktoer.getBankLandkode())
        .valutaKode(aktoer.getValutaKode())
        .build();
  }
}

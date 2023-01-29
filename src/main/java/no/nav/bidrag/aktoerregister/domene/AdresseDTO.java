package no.nav.bidrag.aktoerregister.domene;

import static no.nav.bidrag.aktoerregister.util.StringUtil.isEqual;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.v3.oas.annotations.media.Schema;

// TODO: Kvalitetssikre beskrivelse av adresse-felter.
@Schema(
    description =
        "Representerer navn og/eller adresse for en bidragsaktør. TODO: Beskrivelse av felter må kvalitetssikres.")
@JsonInclude(Include.NON_NULL)
public class AdresseDTO {

  @Schema(description = "Aktørens navn")
  private String navn;

  @Schema(
      description =
          "Første adresselinje inneholder normalt gatenavn, men kan også innehold f.eks c/o.")
  private String adresselinje1;

  @Schema(
      description =
          "Andre adresselinje brukes primært i utlandsadresser, hvor postnr og poststed ikke er tilgjengelig som strukturerte data.")
  private String adresselinje2;

  @Schema(description = "Tredje adresselinje brukes i noen tilfeller til region.")
  private String adresselinje3;

  @Schema(description = "Postnr dersom dette er tilgjengelig som strukturerte data.")
  private String postnr;

  @Schema(description = "Poststed dersom dette er tilgjengelig som strukturerte data.")
  private String poststed;

  @Schema(description = "Land som 3-bokstavs land-kode.")
  private String land;

  public String getNavn() {
    return navn;
  }

  public void setNavn(String navn) {
    this.navn = navn;
  }

  public String getAdresselinje1() {
    return adresselinje1;
  }

  public void setAdresselinje1(String adresselinje1) {
    this.adresselinje1 = adresselinje1;
  }

  public String getAdresselinje2() {
    return adresselinje2;
  }

  public void setAdresselinje2(String adresselinje2) {
    this.adresselinje2 = adresselinje2;
  }

  public String getAdresselinje3() {
    return adresselinje3;
  }

  public void setAdresselinje3(String adresselinje3) {
    this.adresselinje3 = adresselinje3;
  }

  public String getPostnr() {
    return postnr;
  }

  public void setPostnr(String postnr) {
    this.postnr = postnr;
  }

  public String getPoststed() {
    return poststed;
  }

  public void setPoststed(String poststed) {
    this.poststed = poststed;
  }

  public String getLand() {
    return land;
  }

  public void setLand(String land) {
    this.land = land;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof AdresseDTO adresseDTO)) {
      return false;
    }

    return isEqual(this.getAdresselinje1(), adresseDTO.getAdresselinje1())
        && isEqual(this.getAdresselinje2(), adresseDTO.getAdresselinje2())
        && isEqual(this.getAdresselinje3(), adresseDTO.getAdresselinje3())
        && isEqual(this.getLand(), adresseDTO.getLand())
        && isEqual(this.getPostnr(), adresseDTO.getPostnr())
        && isEqual(this.getPoststed(), adresseDTO.getPoststed())
        && isEqual(this.getNavn(), adresseDTO.getNavn());
  }
}

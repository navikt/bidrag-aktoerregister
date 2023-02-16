package no.nav.bidrag.aktoerregister.domene;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
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
}

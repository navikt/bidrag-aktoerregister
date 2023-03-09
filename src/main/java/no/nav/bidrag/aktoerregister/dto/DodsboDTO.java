package no.nav.bidrag.aktoerregister.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Representerer navn for en bidragsaktør.")
public class DodsboDTO {

  @Schema(description = "Navn på kontaktperson for dødsboet.")
  private String kontaktpersion;

  private AdresseDTO adresse;
}

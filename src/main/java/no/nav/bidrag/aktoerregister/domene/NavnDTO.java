package no.nav.bidrag.aktoerregister.domene;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Representerer navn for en bidragsaktør.")
public class NavnDTO {

  @Schema(
      description = "Personens fornavn og eventuelle mellomnavn. Benyttes ikke for samhandlere.")
  private String fornavn;

  @Schema(description = "Personens etternavn eller samhandlerens fulle navn.")
  private String etternavn;
}

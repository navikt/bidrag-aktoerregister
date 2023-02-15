package no.nav.bidrag.aktoerregister.domene;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.bidrag.aktoerregister.domene.enumer.IdenttypeDTO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AktoerIdDTO {

  @Schema(
      description =
          "Identen for aktøren. "
              + "For personer vil dette være FNR eller DNR. "
              + "Ellers benyttes aktørnummer på elleve siffer hvor første siffer er 8 eller 9.")
  private String aktoerId;

  @Schema(
      description =
          "Angir hvilken type ident som er angitt. "
              + "For personer vil dette være FNR eller DNR, som angis med PERSONNUMMER. "
              + "Utover dette benyttes AKTOERNUMMER.")
  private IdenttypeDTO identtype;
}

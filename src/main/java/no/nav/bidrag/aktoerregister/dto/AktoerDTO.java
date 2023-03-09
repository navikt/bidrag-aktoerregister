package no.nav.bidrag.aktoerregister.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.bidrag.aktoerregister.dto.enumer.Gradering;

@Data
@Builder
@JsonInclude(Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class AktoerDTO {

  @Schema(description = "Id for aktøren")
  private AktoerIdDTO aktoerId;

  @Schema(description = "Offentlig id for samhandlere. Angis ikke for personer.")
  private String offentligId;

  @Schema(description = "Type offentlig id. F.eks ORG for norske organisasjonsnummere.")
  private String offentligIdType;

  @Schema(description = "Navn for aktøren")
  private NavnDTO navn;

  @Schema(description = "Gradering for aktøren")
  private Gradering gradering;

  @Schema(description = "Aktørens adresse. Angis ikke for personer.")
  private AdresseDTO adresse;

  @Schema(description = "Språkkoden for aktøren.")
  private String sprakkode;

  @Schema(description = "Lister alle aktørens tidligere identer.")
  private List<AktoerIdDTO> tidligereIdenter;

  @Schema(description = "Personens fødselsdato. Settes for alle personer der fødselsdato er kjent.")
  private String fodtDato;

  @Schema(description = "Personens fødselsdato. Settes for alle personer der fødselsdato er kjent.")
  private String dodDato;

  @Schema(description = "Dødsbo for aktøren")
  private DodsboDTO dodsbo;

  @Schema(description = "Aktørens kontonummer.")
  private KontonummerDTO kontonummer;
}

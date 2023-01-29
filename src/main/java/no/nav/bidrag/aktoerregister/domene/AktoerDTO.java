package no.nav.bidrag.aktoerregister.domene;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class AktoerDTO {

  @Schema(description = "Id for aktøren")
  private AktoerIdDTO aktoerId;

  @Schema(description = "Offentlig id for samhandlere. Angis ikke for personer.")
  private String offentligId;

  @Schema(description = "Type offentlig id. F.eks ORG for norske organisasjonsnummere.")
  private String offentligIdType;

  @Schema(description = "Aktørens adresse. Angis ikke for personer.")
  private AdresseDTO adresse;

  @Schema(description = "Aktørens kontonummer.")
  private KontonummerDTO kontonummer;

  public AktoerDTO() {}

  public AktoerDTO(AktoerIdDTO aktoer) {
    this.aktoerId = aktoer;
  }
}

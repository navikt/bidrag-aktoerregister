package no.nav.bidrag.aktoerregister.domene;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
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

  @Schema(description = "Aktørens navn.")
  private NavnDTO navn;

  @Schema(description = "Personens gradering/diskresjonskode. ")
  private Gradering gradering;

  @Schema(description = "Aktørens adresse. Angis ikke for personer.")
  private AdresseDTO adresse;

  @Schema(description = "Lister alle aktørens identer. Inneholder også gjeldende ident.")
  private Collection<AktoerIdDTO> identer = new ArrayList<>();

  @Schema(
      description = "Personens fødselsdato. Settes for alle personer der fødselsdato er kjent.",
      required = false)
  private LocalDate fodtDato;

  @Schema(
      description =
          "Personens eventuelle dødsdato. Settes kun dersom personen er død og dødsdato er kjent.",
      required = false)
  private LocalDate dodDato;

  @Schema(description = "Aktørens kontonummer.")
  private KontonummerDTO kontonummer;

  public AktoerDTO() {}

  public AktoerDTO(AktoerIdDTO aktoer) {
    this.aktoerId = aktoer;
  }
}

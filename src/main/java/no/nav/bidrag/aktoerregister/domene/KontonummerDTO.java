package no.nav.bidrag.aktoerregister.domene;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(
    description =
        "Representerer kontonummer for en bidragsaktør. For norske kontonummer er det kun norskKontornr som er utfyllt, ellers benyttes de andre feltene for utlandske kontonummer.")
@JsonInclude(Include.NON_NULL)
public class KontonummerDTO {

  @Schema(description = "Norsk kontonummer, 11 siffer.")
  private String norskKontonr;

  @Schema(description = "IBAN angir kontonummeret på et internasjonalt format.")
  private String iban;

  @Schema(description = "SWIFT angir banken på et internasjonalt format.")
  private String swift;

  @Schema(description = "Bankens navn.")
  private String bankNavn;

  @Schema(
      description = "Bankens landkode. TODO: Bestemme representasjon av land. 3-sifret land-kode?")
  private String bankLandkode;

  @Schema(description = "BankCode. Format varierer.")
  private String bankCode;

  @Schema(description = "Kontoens valuta.")
  private String valutaKode;
}

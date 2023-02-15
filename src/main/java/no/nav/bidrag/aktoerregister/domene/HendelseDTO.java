package no.nav.bidrag.aktoerregister.domene;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(
    description =
        "En hendelse signaliserer at enten adresse eller kontonummer for en aktør er oppdatert. Hendelsen inneholder ikke selve oppdateringen.")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HendelseDTO implements Comparable<HendelseDTO> {

  @Schema(
      description = "Hendelsens sekvensnummer. Sekvensnummeret vil alltid øke i nyere hendelser.")
  private int sekvensnummer;

  @Schema(description = "Aktøren som er oppdatert.")
  private AktoerIdDTO aktoerId;

  @Override
  public int compareTo(HendelseDTO other) {
    return Integer.compare(sekvensnummer, other.sekvensnummer);
  }
}

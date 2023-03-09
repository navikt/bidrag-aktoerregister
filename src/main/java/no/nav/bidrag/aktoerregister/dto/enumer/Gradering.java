package no.nav.bidrag.aktoerregister.dto.enumer;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Gradering/Diskresjonskoder:\n\n"
    + "| API-kode                 | TPS-kode | Også omtalt som |\n\n"
    + "| FORTROLIG                | SPFO     | Kode 7          |\n\n"
    + "| STRENGT_FORTROLIG        | SPSF     | Kode 6          |\n\n"
    + "| STRENGT_FORTROLIG_UTLAND | SPSF     | §19             |"
)
public enum Gradering {

  FORTROLIG(TpsKode.SPFO, "Kode 7"),
  STRENGT_FORTROLIG(TpsKode.SPSF, "Kode 6"),
  STRENGT_FORTROLIG_UTLAND(TpsKode.SPSF, "§19");

  Gradering(TpsKode tpskode, String omtaltSom) {

  }
}
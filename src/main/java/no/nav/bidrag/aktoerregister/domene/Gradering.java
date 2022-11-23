package no.nav.bidrag.aktoerregister.domene;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    description =
        """
        Gradering/Diskresjonskoder:

        | API-kode                 | TPS-kode | Også omtalt som |
        | FORTROLIG                | SPFO     | Kode 7          |
        | STRENGT_FORTROLIG        | SPSF     | Kode 6          |
        | STRENGT_FORTROLIG_UTLAND | SPSF     | §19             |
        """)
public enum Gradering {
  FORTROLIG,
  STRENGT_FORTROLIG,
  STRENGT_FORTROLIG_UTLAND;
}

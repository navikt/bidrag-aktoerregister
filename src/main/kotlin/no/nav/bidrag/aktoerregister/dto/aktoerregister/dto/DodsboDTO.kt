package no.nav.bidrag.aktoerregister.dto.aktoerregister.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Representerer navn for en bidragsaktør.")
data class DodsboDTO(

    @Schema(description = "Navn på kontaktperson for dødsboet.")
    val kontaktpersion: String? = null,

    val adresse: AdresseDTO? = null
)

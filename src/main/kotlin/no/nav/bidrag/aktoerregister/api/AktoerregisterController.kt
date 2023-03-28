package no.nav.bidrag.aktoerregister.api

import io.github.oshai.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import no.nav.bidrag.aktoerregister.dto.aktoerregister.dto.AktoerDTO
import no.nav.bidrag.aktoerregister.dto.aktoerregister.dto.AktoerIdDTO
import no.nav.bidrag.aktoerregister.dto.aktoerregister.dto.HendelseDTO
import no.nav.bidrag.aktoerregister.exception.AktoerNotFoundException
import no.nav.bidrag.aktoerregister.service.AktoerregisterService
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

private val LOGGER = KotlinLogging.logger {}

@RestController
@ProtectedWithClaims(issuer = "maskinporten", claimMap = ["scope=nav:bidrag:aktoerregister.read"])
class AktoerregisterController(
    private val aktoerregisterService: AktoerregisterService
) {

    @Operation(
        summary = "Hent informasjon om gitt aktør.",
        description = "For personer returneres kun kontonummer. For andre typer aktører leveres også navn og adresse."
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Aktøren ble funnet."),
        ApiResponse(responseCode = "400", description = "Gitt identtype eller ident er ugyldig.", content = [Content()]),
        ApiResponse(responseCode = "404", description = "Ingen aktør med gitt identtype og ident ble funnet.", content = [Content()])
    )
    @PostMapping(path = ["/aktoer"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun hentAktoer(@RequestBody request: AktoerIdDTO): ResponseEntity<AktoerDTO> {
        return try {
            val aktoer = aktoerregisterService.hentAktoer(request)
            ResponseEntity.ok(aktoer)
        } catch (e: AktoerNotFoundException) {
            LOGGER.info { "Aktoer ${request.aktoerId} ikke funnet." }
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Finner ingen aktør med oppgitt ident", e)
        } catch (e: Exception) {
            LOGGER.error(e) { "Feil ved henting av aktør ${request.aktoerId}" }
            throw ResponseStatusException(INTERNAL_SERVER_ERROR, "Intern tjenestefeil. Problem med oppkobling mot MQ. Prøv igjen senere.", e) //TODO() Oppdatere feilmelding
        }
    }

    @Operation(
        summary = "Tilbyr en liste over aktøroppdateringer.",
        description = "Ingen informasjon om aktøren leveres av denne tjenesten utover aktørIden\n."
                + "Hendelsene legges inn med stigende sekvensnummer. Klienten må selv ta vare på hvilke sekvensnummer som sist er behandlet, og be om å få hendelser fra det neste sekvensnummeret ved neste kall.\n"
                + "Dersom det ikke returneres noen hendelser er ingen av aktørene endret siden siste kall. Samme sekvensnummer må da benyttes i neste kall.\n\n"
                + "Nye hendelser vil alltid ha høyere sekvensnummer enn tidligere hendelser.\n"
                + "Det kan forekomme hull i sekvensnummer-rekken.\n"
                + "Dersom det kommer en hendelse for en aktør med tidligere hendelser (lavere sekvensnummer) er det ikke garantert at de tidligere hendelsene ikke returneres."
    )
    @GetMapping(path = ["/hendelser"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun hentHendelser(
        @Parameter(description = "Angir første sekvensnummer som ønskes hentet. Default-verdi er 0")
        @RequestParam(name = "fraSekvensnummer", defaultValue = "0") fraSekvensnummer: Int = 0,
        @Parameter(description = "Maksimalt antall hendelser som ønskes hentet. Default-verdi er 1000.")
        @RequestParam(name = "antall", defaultValue = "1000") antall: Int = 1000
    ): ResponseEntity<List<HendelseDTO>> {
        return try {
            ResponseEntity.ok(aktoerregisterService.hentHendelser(fraSekvensnummer, antall))
        } catch (e: Exception) {
            LOGGER.error(e) { "Feil ved henting av $antall hendelser fra sekvensnummer $fraSekvensnummer" }
            throw ResponseStatusException(INTERNAL_SERVER_ERROR, "Intern tjenestefeil. Problem ved henting av hendelser. Prøv igjen senere", e)
        }
    }
}
package no.nav.bidrag.aktoerregister.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import no.nav.bidrag.aktoerregister.domene.Aktoer;
import no.nav.bidrag.aktoerregister.domene.AktoerId;
import no.nav.bidrag.aktoerregister.domene.Hendelse;
import no.nav.bidrag.aktoerregister.domene.Identtype;
import no.nav.bidrag.aktoerregister.service.AktoerregisterService;
import no.nav.bidrag.aktoerregister.service.HendelseService;

@RestController
@RequestMapping("/bidrag-aktorer")
public class AktoerregisterController {

    private final HendelseService hendelseService;
    private final AktoerregisterService aktoerregisterService;

    @Autowired
    public AktoerregisterController(HendelseService hendelseService, AktoerregisterService kunderegisterService) {
        this.hendelseService = hendelseService;
        this.aktoerregisterService = kunderegisterService;
    }

    @Operation(summary = "Hent informasjon om gitt aktør.", description = "For personer returneres kun kontonummer. "
            + "For andre typer aktører leveres også navn og adresse.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Aktøren ble funnet."),
            @ApiResponse(responseCode = "400", description = "Gitt identtype eller ident er ugyldig.", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Ingen aktør med gitt identtype og ident ble funnet.", content=@Content())
    })
    @GetMapping("/aktoer/{identtype}/{ident}")
    public ResponseEntity<Aktoer> hentAktoerinformasjon(
            @Parameter(description = "Angir hvilken type ident som er angitt i forespørselen. "
                    + "For personer vil dette være FNR eller DNR, som angis med PERSONNUMMER. "
                    + "Utover dette benyttes AKTOERNUMMER.") @PathVariable(name = "identtype") Identtype identtype,

            @Parameter(description = "Identen for aktøren som skal hentes. "
                    + "For personer vil dette være FNR eller DNR. "
                    + "Ellers benyttes aktørnummer på elleve siffer hvor første siffer er 8.") @PathVariable(name = "ident") String ident) {

        Aktoer kunde = aktoerregisterService.hentAktoer(new AktoerId(ident, identtype));

        if (kunde == null) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.ok(kunde);
    }

    @GetMapping("/hendelser")
    public ResponseEntity<List<Hendelse>> hentKontonummerHendelser(
            @RequestParam(name = "fraSekvensnummer", defaultValue = "1") Integer fraSekvensnummer,
            @RequestParam(name = "antall", defaultValue = "10000") Integer antall) {

        List<Hendelse> hendelser = hendelseService
                .hentHendelser(fraSekvensnummer, antall);

        return ResponseEntity.ok(hendelser);
    }
}

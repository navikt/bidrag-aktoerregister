package no.nav.bidrag.aktoerregister.domene;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(Include.NON_NULL)
public class Aktoer {
    @Schema(description = "Id for aktøren")
    private final AktoerId aktoerId;

    @Schema(description = "Aktørens adresse. Angis ikke for personer.")
    private Adresse adresse;

    @Schema(description = "Aktørens kontonummer.")
    private Kontonummer kontonummer;

    public Aktoer(AktoerId aktoer) {
        this.aktoerId = aktoer;
    }

    public Adresse getAdresse() {
        return adresse;
    }

    public void setAdresse(Adresse adresse) {
        this.adresse = adresse;
    }

    public Kontonummer getKontonummer() {
        return kontonummer;
    }

    public void setKontonummer(Kontonummer kontonummer) {
        this.kontonummer = kontonummer;
    }

    public AktoerId getAktoerId() {
        return aktoerId;
    }

}

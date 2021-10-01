package no.nav.bidrag.aktoerregister.domene;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(Include.NON_NULL)
public class AktoerDTO {
    @Schema(description = "Id for aktøren")
    private AktoerIdDTO aktoerId;

    @Schema(description = "Aktørens adresse. Angis ikke for personer.")
    private AdresseDTO adresse;

    @Schema(description = "Aktørens kontonummer.")
    private KontonummerDTO kontonummer;

    public AktoerDTO(){}

    public AktoerDTO(AktoerIdDTO aktoer) {
        this.aktoerId = aktoer;
    }

    public AdresseDTO getAdresse() {
        return adresse;
    }

    public void setAdresse(AdresseDTO adresse) {
        this.adresse = adresse;
    }

    public KontonummerDTO getKontonummer() {
        return kontonummer;
    }

    public void setKontonummer(KontonummerDTO kontonummer) {
        this.kontonummer = kontonummer;
    }

    public AktoerIdDTO getAktoerId() {
        return aktoerId;
    }

    public void setAktoerId(AktoerIdDTO aktoerId) {
        this.aktoerId = aktoerId;
    }

}

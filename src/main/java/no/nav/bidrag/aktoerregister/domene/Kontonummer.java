package no.nav.bidrag.aktoerregister.domene;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representerer kontonummer for en bidragsaktør. For norske kontonummer er det kun norskKontornr som er utfyllt, ellers benyttes de andre feltene for utlandske kontonummer.")
@JsonInclude(Include.NON_NULL)
public class Kontonummer {
    
    @Schema(description = "Norsk kontonummer, 11 siffer.")
    private String norskKontonr;
    
    @Schema(description = "IBAN angir kontonummeret på et internasjonalt format.")
    private String iban;
    
    @Schema(description = "SWIFT angir banken på et internasjonalt format.")
    private String swift;
    
    @Schema(description = "Bankens navn.")
    private String bankNavn;
    
    @Schema(description = "Bankens landkode. TODO: Bestemme representasjon av land. 3-sifret land-kode?")
    private String bankLandkode;
    
    @Schema(description = "Kontoens valuta.")
    private String valutaKode;

    public String getNorskKontonr() {
        return norskKontonr;
    }

    public void setNorskKontonr(String norskKontonr) {
        this.norskKontonr = norskKontonr;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getSwift() {
        return swift;
    }

    public void setSwift(String swift) {
        this.swift = swift;
    }

    public String getBankNavn() {
        return bankNavn;
    }

    public void setBankNavn(String bankNavn) {
        this.bankNavn = bankNavn;
    }

    public String getBankLandkode() {
        return bankLandkode;
    }

    public void setBankLandkode(String bankLandkode) {
        this.bankLandkode = bankLandkode;
    }

    public String getValutaKode() {
        return valutaKode;
    }

    public void setValutaKode(String valutaKode) {
        this.valutaKode = valutaKode;
    }

}

package no.nav.bidrag.aktoerregister.domene;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Kontonummer {
    private String norskKontonr;
    private String iban;
    private String swift;
    private String bankNavn;
    private String bankLandkode;
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

package no.nav.bidrag.aktoerregister.domene;

public class Aktoer {
    private final AktoerId aktoer;
    private Adresse adresse;
    private Kontonummer kontonummer;

    public Aktoer(AktoerId aktoer) {
        this.aktoer = aktoer;
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

    public AktoerId getKunde() {
        return aktoer;
    }

}

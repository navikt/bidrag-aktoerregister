package no.nav.bidrag.aktoerregister.domene;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "En hendelse signaliserer at enten adresse eller kontonummer for en aktør er oppdatert. Hendelsen inneholder ikke selve oppdateringen.")
public class Hendelse implements Comparable<Hendelse>{
    
    @Schema(description = "Hendelsens sekvensnummer. Sekvensnummeret vil alltid øke i nyere hendelser.")
    private int sekvensnummer;
    
    @Schema(description = "Aktøren som er oppdatert.")
    private AktoerId aktoerId;

    public int getSekvensnummer() {
    	return sekvensnummer;
    }

    public void setSekvensnummer(int sekvensnummer) {
    	this.sekvensnummer = sekvensnummer;
    }

    public AktoerId getAktoerId() {
        return aktoerId;
    }

    public void setAktoerId(AktoerId aktoerId) {
        this.aktoerId = aktoerId;
    }

    @Override
    public int compareTo(Hendelse other) {
        return Integer.compare(sekvensnummer, other.sekvensnummer);
    }
}
package no.nav.bidrag.aktoerregister.domene;

public class Hendelse implements Comparable<Hendelse>{
    private int sekvensnummer;
    private AktoerId kundeId;

    public int getSekvensnummer() {
    	return sekvensnummer;
    }

    public void setSekvensnummer(int sekvensnummer) {
    	this.sekvensnummer = sekvensnummer;
    }

    public AktoerId getKundeId() {
        return kundeId;
    }

    public void setKundeId(AktoerId kundeId) {
        this.kundeId = kundeId;
    }

    @Override
    public int compareTo(Hendelse other) {
        return Integer.compare(sekvensnummer, other.sekvensnummer);
    }
}
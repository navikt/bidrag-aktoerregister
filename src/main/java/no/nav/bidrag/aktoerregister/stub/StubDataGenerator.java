package no.nav.bidrag.aktoerregister.stub;

import java.util.List;

import no.nav.bidrag.aktoerregister.domene.Adresse;
import no.nav.bidrag.aktoerregister.domene.AktoerId;
import no.nav.bidrag.aktoerregister.domene.Identtype;
import no.nav.bidrag.aktoerregister.domene.Kontonummer;

public class StubDataGenerator extends StubHelper {
    private static final List<String> NAVN_KOMPONENTER_DEL1 = List.of("Søt", "Skummel", "Vennlig", "Sensuell", "Høylytt", "Stille", "Lang", "Bred");
    private static final List<String> NAVN_KOMPONENTER_DEL2 = List.of("Kommune", "Skole", "Barnehage", "Lege", "Lensmannskontor", "Fengsel", "Advokat", "Etat");
    private static final List<String> GATER = List.of("Ibsens gate", "Storgata", "Kongens gate");
    private static final List<String> POSTSTEDER = List.of("Oslo", "Lislefjødd", "Hamar");
    
    public static AktoerId randomKunde(Identtype ...identtyper) {
        switch(random(identtyper)) {
        case AKTOERNUMMER:
            return new AktoerId("8"+random(0,99999)+random(0,99999), Identtype.AKTOERNUMMER);
            
        case PERSONNUMMER:
            return new AktoerId(random(10,28)+ "0" + random(1,9)+random(80, 99)+"12345", Identtype.PERSONNUMMER);
        }
        return null;
    }
    
    public static Adresse nyAresse() {
        Adresse adresse = new Adresse();
        adresse.setNavn(random(NAVN_KOMPONENTER_DEL1) + " " + random(NAVN_KOMPONENTER_DEL2));
        switch(random(Adressetype.values())) {
        
        case POSTBOKS:
            adresse.setAdresselinje1("Postboks " + random(1, 999));
            adresse.setPostnr("" + random(1000, 9999));
            adresse.setPoststed(random(POSTSTEDER));
            break;
            
        case GATE:
            adresse.setAdresselinje1(random(GATER) + " " + random(1, 100));
            adresse.setPostnr("" + random(1000, 9999));
            adresse.setPoststed(random(POSTSTEDER));
            break;
            
        case UTLAND:
            adresse.setAdresselinje1("Bedriftssenter");
            adresse.setAdresselinje2("Lyckliga gatan " + random(1,100));
            adresse.setAdresselinje3("Göteborg");
            adresse.setLand("SWE");
            break;
        }
        return adresse;
    }
    
    public static Kontonummer nyttKontonummer() {
        Kontonummer kontonummer = new Kontonummer();
        switch(random(Kontotype.values())) {
        case NORSK:
            kontonummer.setNorskKontonr(random(1000,9999) + "" + random(10, 99) + "" + random(10000, 99999));
            break;
            
        case UTLANDSK:
            kontonummer.setIban("PL" + random(100000000,999999999) + random(100000000,999999999) + random(100000000,999999999));
            kontonummer.setSwift("BIGBPLPW");
            kontonummer.setBankNavn("Millenium");
            kontonummer.setBankLandkode("POL");
            kontonummer.setValutaKode("PLN");
        }
        
        return kontonummer;
    }

    private static enum Adressetype {
        POSTBOKS, GATE, UTLAND;
    }

    private static enum Kontotype {
        NORSK, UTLANDSK;
    }
}

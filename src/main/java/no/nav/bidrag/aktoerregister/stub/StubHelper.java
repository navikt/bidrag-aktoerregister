package no.nav.bidrag.aktoerregister.stub;

import java.util.List;

import no.nav.bidrag.aktoerregister.domene.AktoerId;
import no.nav.bidrag.aktoerregister.domene.Identtype;

public class StubHelper {
    public static <V> V random(List<V> liste) {
        return liste.get(random(0, liste.size()));
    }

    public static <V> V random(V[] liste) {
        return liste[random(0, liste.length)];
    }
    
    public static int random(int min, int max) {
        return (int) (Math.random() * (max - min)) + min;
    }
    
    public static boolean randomSannsynlighet(double sannsynlighet) {
        return sannsynlighet <= Math.random();
    }
    
    public static AktoerId randomKunde(Identtype ...identtyper) {
        switch(random(identtyper)) {
        case AKTOERNUMMER:
            return new AktoerId("989-"+random(0,999), Identtype.AKTOERNUMMER);
            
        case PERSONNUMMER:
            return new AktoerId(random(10,28)+ "0" + random(1,9)+random(80, 99)+"12345", Identtype.PERSONNUMMER);
        }
        return null;
    }
}

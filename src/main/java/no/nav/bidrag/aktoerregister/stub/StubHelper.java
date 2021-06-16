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
    
}

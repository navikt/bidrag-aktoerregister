package no.nav.bidrag.aktoerregister.service;

import no.nav.bidrag.aktoerregister.domene.Adresse;
import no.nav.bidrag.aktoerregister.domene.Aktoer;
import no.nav.bidrag.aktoerregister.domene.AktoerId;
import no.nav.bidrag.aktoerregister.domene.Kontonummer;

public interface AktoerregisterService {
    void oppdaterAdresse(AktoerId kunde, Adresse nyAdresse);
    
    void oppdaterKonto(AktoerId kunde, Kontonummer nyKonto);
    
    Aktoer hentAktoer(AktoerId kundeId);
}

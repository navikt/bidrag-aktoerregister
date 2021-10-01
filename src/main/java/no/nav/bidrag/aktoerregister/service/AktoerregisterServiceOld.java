package no.nav.bidrag.aktoerregister.service;

import no.nav.bidrag.aktoerregister.domene.AdresseDTO;
import no.nav.bidrag.aktoerregister.domene.AktoerDTO;
import no.nav.bidrag.aktoerregister.domene.AktoerIdDTO;
import no.nav.bidrag.aktoerregister.domene.KontonummerDTO;

public interface AktoerregisterServiceOld {
    void oppdaterAdresse(AktoerIdDTO aktoerId, AdresseDTO nyAdresse);
    
    void oppdaterKonto(AktoerIdDTO aktoerId, KontonummerDTO nyKonto);
    
    AktoerDTO hentAktoer(AktoerIdDTO aktoerId);
}

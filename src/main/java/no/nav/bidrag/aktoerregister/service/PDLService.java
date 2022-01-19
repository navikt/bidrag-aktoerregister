package no.nav.bidrag.aktoerregister.service;

import no.nav.bidrag.aktoerregister.domene.AktoerDTO;
import no.nav.bidrag.aktoerregister.domene.PersonDTO;

public interface PDLService {
    AktoerDTO hentAktoer(String id);

    PersonDTO hentRawAktoer(String id);
}

package no.nav.bidrag.aktoerregister.service;

import com.fasterxml.jackson.databind.JsonNode;
import no.nav.bidrag.aktoerregister.domene.AktoerDTO;
import no.nav.bidrag.aktoerregister.domene.PersonDTO;
import no.nav.bidrag.aktoerregister.exception.AktoerNotFoundException;
import no.nav.bidrag.aktoerregister.exception.PDLServiceException;

public interface PDLService {
    AktoerDTO hentAktoer(String id);

    PersonDTO hentRawAktoer(String id) throws PDLServiceException, AktoerNotFoundException;
}

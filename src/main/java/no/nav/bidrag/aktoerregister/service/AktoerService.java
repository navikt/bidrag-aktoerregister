package no.nav.bidrag.aktoerregister.service;

import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;

public interface AktoerService {

  Aktoer hentAktoer(String aktoerIdent);
}

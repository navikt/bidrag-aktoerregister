package no.nav.bidrag.aktoerregister.service;

import no.nav.bidrag.aktoerregister.persistence.entities.Aktør;

public interface AktoerService {

  Aktør hentAktoer(String aktoerIdent);
}

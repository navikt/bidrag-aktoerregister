package no.nav.bidrag.aktoerregister.service;

import no.nav.bidrag.aktoerregister.domene.AktoerDTO;
import no.nav.bidrag.aktoerregister.domene.AktoerIdDTO;

public interface AktoerService {

  AktoerDTO hentAktoer(AktoerIdDTO aktoerId);
}

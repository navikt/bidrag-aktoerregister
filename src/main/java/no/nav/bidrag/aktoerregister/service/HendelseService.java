package no.nav.bidrag.aktoerregister.service;

import java.util.List;

import no.nav.bidrag.aktoerregister.domene.AktoerIdDTO;
import no.nav.bidrag.aktoerregister.domene.HendelseDTO;

public interface HendelseService {

    List<HendelseDTO> hentHendelser(int fraSekvensnummer, int antallHendelser);

    void registrerHendelse(AktoerIdDTO aktoerId);

}

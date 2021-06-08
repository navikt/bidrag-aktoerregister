package no.nav.bidrag.aktoerregister.service;

import java.util.List;

import no.nav.bidrag.aktoerregister.domene.AktoerId;
import no.nav.bidrag.aktoerregister.domene.Hendelse;

public interface HendelseService {

    List<Hendelse> hentHendelser(int fraSekvensnummer, int antallHendelser);

    void registrerHendelse(AktoerId kundeId);

}

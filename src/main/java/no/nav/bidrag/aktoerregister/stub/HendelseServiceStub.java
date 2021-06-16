package no.nav.bidrag.aktoerregister.stub;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import no.nav.bidrag.aktoerregister.domene.AktoerId;
import no.nav.bidrag.aktoerregister.domene.Hendelse;
import no.nav.bidrag.aktoerregister.service.HendelseService;

@Component
public class HendelseServiceStub implements HendelseService {
    private final List<Hendelse> hendelser = new ArrayList<>();

    public void registrerHendelse(AktoerId aktoerId) {
        Hendelse hendelse = new Hendelse();
        hendelse.setAktoerId(aktoerId);
        hendelse.setSekvensnummer(hendelser.size() + 1);
        hendelser.add(hendelse);
    }
    
    public List<Hendelse> hentHendelser(int fraSekvensnummer, int antallHendelser) {
        return hendelser
        .stream()
        .filter((h) -> h.getSekvensnummer() >= fraSekvensnummer)
        .limit(antallHendelser)
        .collect(Collectors.toList());
    }
}

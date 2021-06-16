package no.nav.bidrag.aktoerregister.stub;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import no.nav.bidrag.aktoerregister.domene.Adresse;
import no.nav.bidrag.aktoerregister.domene.Aktoer;
import no.nav.bidrag.aktoerregister.domene.AktoerId;
import no.nav.bidrag.aktoerregister.domene.Kontonummer;
import no.nav.bidrag.aktoerregister.service.AktoerregisterService;
import no.nav.bidrag.aktoerregister.service.HendelseService;

@Component
public class AktoerregisterServiceStub implements AktoerregisterService {

    private final HendelseService hendelseService;
    private final Map<AktoerId, Aktoer> aktoerregister = new HashMap<>();

    @Autowired
    public AktoerregisterServiceStub(HendelseService hendelseService) {
        this.hendelseService = hendelseService;
    }

    @Override
    public void oppdaterAdresse(AktoerId aktoerId, Adresse nyAdresse) {
        finnEllerOpprettAktoer(aktoerId)
                .setAdresse(nyAdresse);
        
        hendelseService.registrerHendelse(aktoerId);
    }

    @Override
    public void oppdaterKonto(AktoerId aktoerId, Kontonummer nyKonto) {
        finnEllerOpprettAktoer(aktoerId)
                .setKontonummer(nyKonto);

        hendelseService.registrerHendelse(aktoerId);
    }

    private synchronized Aktoer finnEllerOpprettAktoer(AktoerId aktoerId) {
        Aktoer aktoer = aktoerregister.get(aktoerId);
        if (aktoer == null) {
            aktoer = new Aktoer(aktoerId);
            aktoerregister.put(aktoerId, aktoer);
        }
        return aktoer;
    }

    @Override
    public Aktoer hentAktoer(AktoerId aktoerId) {
        return aktoerregister.get(aktoerId);
    }
}

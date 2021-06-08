package no.nav.bidrag.aktoerregister.stub;

import static no.nav.bidrag.aktoerregister.stub.StubDataGenerator.nyAresse;
import static no.nav.bidrag.aktoerregister.stub.StubDataGenerator.nyttKontonummer;
import static no.nav.bidrag.aktoerregister.stub.StubDataGenerator.randomKunde;
import static no.nav.bidrag.aktoerregister.stub.StubHelper.random;
import static no.nav.bidrag.aktoerregister.stub.StubHelper.randomSannsynlighet;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import no.nav.bidrag.aktoerregister.domene.AktoerId;
import no.nav.bidrag.aktoerregister.domene.Identtype;
import no.nav.bidrag.aktoerregister.service.AktoerregisterService;

@Component
public class StubRunner implements CommandLineRunner {

    @Autowired
    private AktoerregisterService service;

    private final List<AktoerId> registrerteKunder = new ArrayList<>();

    @Override
    public void run(String... args) throws Exception {
        registrerHendelser(1000);
    }

    private void registrerHendelser(int antall) {
        for (int i = 0; i < antall; i++) {
            registrerHendelse();
        }
    }

    private void registrerHendelse() {
        if (randomSannsynlighet(.1) || registrerteKunder.isEmpty()) {
            registrerNyKunde();

        } else {
            oppdaterKunde(random(registrerteKunder));

        }
    }

    private void registrerNyKunde() {
        AktoerId kundeId = randomKunde(Identtype.AKTOERNUMMER, Identtype.PERSONNUMMER);
        registrerteKunder.add(kundeId);
        if (Identtype.AKTOERNUMMER.equals(kundeId.getIdenttype())) {
            service.oppdaterAdresse(kundeId, nyAresse());
        }
        service.oppdaterKonto(kundeId, nyttKontonummer());
    }

    private void oppdaterKunde(AktoerId kundeId) {
        // Det er kun adresser som registreres
        if (Identtype.PERSONNUMMER.equals(kundeId.getIdenttype()) || randomSannsynlighet(.3)) {
            service.oppdaterKonto(kundeId, nyttKontonummer());

        } else {
            service.oppdaterAdresse(kundeId, nyAresse());

        }
    }

}

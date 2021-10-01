package no.nav.bidrag.aktoerregister.stub;

import static no.nav.bidrag.aktoerregister.stub.StubDataGenerator.nyAresse;
import static no.nav.bidrag.aktoerregister.stub.StubDataGenerator.nyttKontonummer;
import static no.nav.bidrag.aktoerregister.stub.StubDataGenerator.randomAktoer;
import static no.nav.bidrag.aktoerregister.stub.StubHelper.random;
import static no.nav.bidrag.aktoerregister.stub.StubHelper.randomSannsynlighet;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import no.nav.bidrag.aktoerregister.domene.AktoerIdDTO;
import no.nav.bidrag.aktoerregister.domene.IdenttypeDTO;
import no.nav.bidrag.aktoerregister.service.AktoerregisterServiceOld;

@Component
public class StubRunner implements CommandLineRunner {

    @Autowired
    private AktoerregisterServiceOld service;

    private final List<AktoerIdDTO> registrerteAktoerer = new ArrayList<>();

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
        if (randomSannsynlighet(.1) || registrerteAktoerer.isEmpty()) {
            registrerNyAktoer();

        } else {
            oppdaterAktoer(random(registrerteAktoerer));

        }
    }

    private void registrerNyAktoer() {
        AktoerIdDTO aktoerId = randomAktoer(IdenttypeDTO.AKTOERNUMMER, IdenttypeDTO.PERSONNUMMER);
        registrerteAktoerer.add(aktoerId);
        if (IdenttypeDTO.AKTOERNUMMER.equals(aktoerId.getIdenttype())) {
            service.oppdaterAdresse(aktoerId, nyAresse());
        }
        service.oppdaterKonto(aktoerId, nyttKontonummer());
    }

    private void oppdaterAktoer(AktoerIdDTO aktoerId) {
        // Det er kun adresser som registreres
        if (IdenttypeDTO.PERSONNUMMER.equals(aktoerId.getIdenttype()) || randomSannsynlighet(.3)) {
            service.oppdaterKonto(aktoerId, nyttKontonummer());

        } else {
            service.oppdaterAdresse(aktoerId, nyAresse());

        }
    }

}

package no.nav.bidrag.aktoerregister.stub;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import no.nav.bidrag.aktoerregister.domene.AdresseDTO;
import no.nav.bidrag.aktoerregister.domene.AktoerDTO;
import no.nav.bidrag.aktoerregister.domene.AktoerIdDTO;
import no.nav.bidrag.aktoerregister.domene.KontonummerDTO;
import no.nav.bidrag.aktoerregister.service.AktoerregisterServiceOld;
import no.nav.bidrag.aktoerregister.service.HendelseService;

@Component
public class AktoerregisterServiceStub implements AktoerregisterServiceOld {

    private final HendelseService hendelseService;
    private final Map<AktoerIdDTO, AktoerDTO> aktoerregister = new HashMap<>();

    @Autowired
    public AktoerregisterServiceStub(HendelseService hendelseService) {
        this.hendelseService = hendelseService;
    }

    @Override
    public void oppdaterAdresse(AktoerIdDTO aktoerId, AdresseDTO nyAdresse) {
        finnEllerOpprettAktoer(aktoerId)
                .setAdresse(nyAdresse);
        
        hendelseService.registrerHendelse(aktoerId);
    }

    @Override
    public void oppdaterKonto(AktoerIdDTO aktoerId, KontonummerDTO nyKonto) {
        finnEllerOpprettAktoer(aktoerId)
                .setKontonummer(nyKonto);

        hendelseService.registrerHendelse(aktoerId);
    }

    private synchronized AktoerDTO finnEllerOpprettAktoer(AktoerIdDTO aktoerId) {
        AktoerDTO aktoer = aktoerregister.get(aktoerId);
        if (aktoer == null) {
            aktoer = new AktoerDTO(aktoerId);
            aktoerregister.put(aktoerId, aktoer);
        }
        return aktoer;
    }

    @Override
    public AktoerDTO hentAktoer(AktoerIdDTO aktoerId) {
        return aktoerregister.get(aktoerId);
    }
}

package no.nav.bidrag.aktoerregister.service;

import java.util.Comparator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import no.nav.bidrag.aktoerregister.domene.AktoerDTO;
import no.nav.bidrag.aktoerregister.domene.AktoerIdDTO;
import no.nav.bidrag.aktoerregister.domene.HendelseDTO;
import no.nav.bidrag.aktoerregister.domene.enumer.IdenttypeDTO;
import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;
import no.nav.bidrag.aktoerregister.persistence.entities.Hendelse;
import no.nav.bidrag.aktoerregister.persistence.repository.AktoerRepository;
import no.nav.bidrag.aktoerregister.persistence.repository.HendelseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AktoerregisterServiceImpl implements AktoerregisterService {

  private final AktoerRepository aktoerRepository;
  private final HendelseRepository hendelseRepository;
  private final AktoerService tpsService;
  private final AktoerService tssService;
  private final ConversionService conversionService;

  @Autowired
  public AktoerregisterServiceImpl(
      AktoerRepository aktoerRepository,
      HendelseRepository hendelseRepository,
      @Qualifier("TPSServiceImpl") AktoerService tpsService,
      @Qualifier("TSSServiceImpl") AktoerService tssService,
      ConversionService conversionService) {
    this.conversionService = conversionService;
    this.aktoerRepository = aktoerRepository;
    this.hendelseRepository = hendelseRepository;
    this.tpsService = tpsService;
    this.tssService = tssService;
  }

  @Override
  public AktoerDTO hentAktoer(AktoerIdDTO aktoerId) {
    String aktoerIdent = aktoerId.getAktoerId();

    Aktoer aktoer = hentAktoerFromDB(aktoerIdent);
    if (aktoer != null) {
      log.trace("Aktør {} funnet i databasen", aktoerIdent);
    } else if (aktoerId.getIdenttype().equals(IdenttypeDTO.AKTOERNUMMER)) {
      log.trace("Henter aktør {} fra TSS", aktoerIdent);
      aktoer = lagreAktoer(tssService.hentAktoer(aktoerIdent));
    } else {
      log.trace("Henter aktør {} fra TPS", aktoerIdent);
      aktoer = lagreAktoer(tpsService.hentAktoer(aktoerIdent));
    }
    return conversionService.convert(aktoer, AktoerDTO.class);
  }

  @Override
  public Aktoer hentAktoerFromDB(String aktoerIdent) {
    return aktoerRepository.getAktoer(aktoerIdent);
  }

  @Override
  public List<HendelseDTO> hentHendelser(int sekvensunummer, int antallHendelser) {
    List<Hendelse> hendelser = hendelseRepository.hentHendelser(sekvensunummer, antallHendelser);
    return hendelser.stream()
        .map(
            hendelse -> {
              AktoerIdDTO aktoerIdDTO = AktoerIdDTO.builder()
                  .aktoerId(hendelse.getAktoer().getAktoerIdent())
                  .identtype(IdenttypeDTO.valueOf(hendelse.getAktoer().getAktoerType()))
                  .build();
              return HendelseDTO.builder()
                  .sekvensnummer(hendelse.getSekvensnummer())
                  .aktoerId(aktoerIdDTO)
                  .build();
            })
        .sorted(Comparator.comparingInt(HendelseDTO::getSekvensnummer))
        .toList();
  }

  private Aktoer lagreAktoer(Aktoer aktoer) {
    return aktoerRepository.opprettEllerOppdaterAktoer(aktoer);
  }

  @Transactional
  @Override
  public void oppdaterAktoer(Aktoer aktoer) {
    aktoerRepository.opprettEllerOppdaterAktoer(aktoer);
  }

  @Transactional
  @Override
  public void oppdaterAktoerer(List<Aktoer> aktoerliste) {
    aktoerRepository.opprettEllerOppdaterAktoerer(aktoerliste);
    hendelseRepository.opprettHendelser(aktoerliste);
  }

  @Override
  public void slettAktoer(String aktoerIdent) {
    aktoerRepository.deleteAktoer(aktoerIdent);
  }
}

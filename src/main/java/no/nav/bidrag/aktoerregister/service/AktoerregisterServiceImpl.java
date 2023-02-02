package no.nav.bidrag.aktoerregister.service;

import java.util.Comparator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import no.nav.bidrag.aktoerregister.domene.AktoerDTO;
import no.nav.bidrag.aktoerregister.domene.AktoerIdDTO;
import no.nav.bidrag.aktoerregister.domene.HendelseDTO;
import no.nav.bidrag.aktoerregister.domene.IdenttypeDTO;
import no.nav.bidrag.aktoerregister.exception.AktoerNotFoundException;
import no.nav.bidrag.aktoerregister.exception.MQServiceException;
import no.nav.bidrag.aktoerregister.exception.TPSServiceException;
import no.nav.bidrag.aktoerregister.exception.TSSServiceException;
import no.nav.bidrag.aktoerregister.mapper.AktoerMapper;
import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;
import no.nav.bidrag.aktoerregister.persistence.entities.Hendelse;
import no.nav.bidrag.aktoerregister.persistence.repository.AktoerRepository;
import no.nav.bidrag.aktoerregister.persistence.repository.HendelseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AktoerregisterServiceImpl implements AktoerregisterService {

  private final AktoerRepository aktoerRepository;
  private final HendelseRepository hendelseRepository;
  private final TPSService tpsService;
  private final TSSService tssService;
  private final AktoerMapper aktoerMapper;

  @Autowired
  public AktoerregisterServiceImpl(
      AktoerRepository aktoerRepository,
      HendelseRepository hendelseRepository,
      TPSService tpsService,
      TSSService tssService) {
    this.aktoerRepository = aktoerRepository;
    this.hendelseRepository = hendelseRepository;
    this.tpsService = tpsService;
    this.tssService = tssService;
    aktoerMapper = new AktoerMapper();
  }

  @Override
  public AktoerDTO hentAktoer(AktoerIdDTO aktoerId)
      throws MQServiceException, TSSServiceException, AktoerNotFoundException, TPSServiceException {
    Aktoer aktoerDTOFromDB = hentAktoerFromDB(aktoerId.getAktoerId());
    if (aktoerDTOFromDB != null) {
      log.trace("Aktør {} funnet i databasen", aktoerId.getAktoerId());
      return aktoerMapper.toDomain(aktoerDTOFromDB);
    }

    // Aktoer does not exist in our DB, and we need to fetch it from TSS or TPS
    AktoerDTO aktoerDTO;
    if (aktoerId.getIdenttype().equals(IdenttypeDTO.AKTOERNUMMER)) {
      log.trace("Henter aktør {} fra TSS", aktoerId.getAktoerId());
      aktoerDTO = tssService.hentAktoer(aktoerId);
    } else {
      log.trace("Henter aktør {} fra TPS", aktoerId.getAktoerId());
      aktoerDTO = tpsService.hentAktoer(aktoerId);
    }
    return addAktoer(aktoerDTO);
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
              HendelseDTO hendelseDTO = new HendelseDTO();
              hendelseDTO.setSekvensnummer(hendelse.getSekvensnummer());
              AktoerIdDTO aktoerIdDTO = new AktoerIdDTO();
              aktoerIdDTO.setAktoerId(hendelse.getAktoer().getAktoerIdent());
              aktoerIdDTO.setIdenttype(IdenttypeDTO.valueOf(hendelse.getAktoer().getAktoerType()));
              hendelseDTO.setAktoerId(aktoerIdDTO);
              return hendelseDTO;
            })
        .sorted(Comparator.comparingInt(HendelseDTO::getSekvensnummer))
        .toList();
  }

  private AktoerDTO addAktoer(AktoerDTO aktoerDTO) {
    Aktoer aktoer = aktoerMapper.toPersistence(aktoerDTO);

    Aktoer savedAktoer = aktoerRepository.insertOrUpdateAktoer(aktoer);

    return aktoerMapper.toDomain(savedAktoer);
  }

  @Transactional
  @Override
  public void oppdaterAktoer(Aktoer updatedAktoer) {
    Aktoer existingAktoer = hentAktoerFromDB(updatedAktoer.getAktoerIdent());
    existingAktoer.setOffentligId(updatedAktoer.getOffentligId());
    existingAktoer.setOffentligIdType(updatedAktoer.getOffentligIdType());
    existingAktoer.setAdresse(updatedAktoer.getAdresse());
    existingAktoer.setKontonummer(updatedAktoer.getKontonummer());

    aktoerRepository.insertOrUpdateAktoer(existingAktoer);
  }

  @Transactional
  @Override
  public void oppdaterAktoerer(List<Aktoer> updatedAktoerList) {
    aktoerRepository.insertOrUpdateAktoerer(updatedAktoerList);
    hendelseRepository.insertHendelser(updatedAktoerList);
  }

  @Override
  public void slettAktoer(String aktoerIdent) {
    aktoerRepository.deleteAktoer(aktoerIdent);
  }
}

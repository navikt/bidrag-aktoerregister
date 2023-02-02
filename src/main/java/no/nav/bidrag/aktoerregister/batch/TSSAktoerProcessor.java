package no.nav.bidrag.aktoerregister.batch;

import no.nav.bidrag.aktoerregister.domene.AktoerDTO;
import no.nav.bidrag.aktoerregister.domene.AktoerIdDTO;
import no.nav.bidrag.aktoerregister.domene.IdenttypeDTO;
import no.nav.bidrag.aktoerregister.exception.AktoerNotFoundException;
import no.nav.bidrag.aktoerregister.exception.MQServiceException;
import no.nav.bidrag.aktoerregister.exception.TSSServiceException;
import no.nav.bidrag.aktoerregister.mapper.AktoerMapper;
import no.nav.bidrag.aktoerregister.mapper.Mapper;
import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;
import no.nav.bidrag.aktoerregister.service.TSSService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TSSAktoerProcessor implements ItemProcessor<Aktoer, TSSAktoerProcessorResult> {

  private final TSSService tssService;

  private final Mapper<AktoerDTO, Aktoer> aktoerMapper;

  private final Logger logger = LoggerFactory.getLogger(TSSAktoerProcessor.class);

  @Autowired
  public TSSAktoerProcessor(TSSService tssService) {
    this.tssService = tssService;
    this.aktoerMapper = new AktoerMapper();
  }

  @Override
  public TSSAktoerProcessorResult process(Aktoer aktoer)
      throws MQServiceException, TSSServiceException {
    AktoerIdDTO aktoerIdDTO = new AktoerIdDTO(aktoer.getAktoerIdent(), IdenttypeDTO.valueOf(aktoer.getAktoerType()));
    try {
      AktoerDTO tssAktoerDTO = tssService.hentAktoer(aktoerIdDTO);
      AktoerDTO dbAktoerDTO = aktoerMapper.toDomain(aktoer);
      if (!tssAktoerDTO.equals(dbAktoerDTO)) {
        // Oppdaterer eksisterende Aktoer
        Aktoer updatedAktoer = aktoerMapper.toPersistence(tssAktoerDTO);
        aktoer.setOffentligId(updatedAktoer.getOffentligId());
        aktoer.setOffentligIdType(updatedAktoer.getOffentligIdType());
        aktoer.setAdresse(updatedAktoer.getAdresse());
        aktoer.setKontonummer(updatedAktoer.getKontonummer());
        return new TSSAktoerProcessorResult(aktoer, AktoerStatus.UPDATED);
      }
    } catch (MQServiceException | TSSServiceException e) {
      logger.error(e.getMessage(), e);
      throw e;
    } catch (AktoerNotFoundException e) {
      return new TSSAktoerProcessorResult(null, AktoerStatus.NOT_FOUND);
    }
    return new TSSAktoerProcessorResult(null, AktoerStatus.NOT_UPDATED);
  }
}

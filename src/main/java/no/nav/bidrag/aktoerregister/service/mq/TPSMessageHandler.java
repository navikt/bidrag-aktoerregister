package no.nav.bidrag.aktoerregister.service.mq;

import no.nav.bidrag.aktoerregister.domene.AktoerDTO;
import no.nav.bidrag.aktoerregister.domene.KontonummerDTO;
import no.nav.bidrag.aktoerregister.service.AktoerregisterService;
import no.nav.bidrag.aktoerregister.util.JsonUtil;
import no.rtv.namespacetps.DistribusjonsMelding;
import no.rtv.namespacetps.TAnnullering;
import no.rtv.namespacetps.Tgironorsk;
import no.rtv.namespacetps.Tgiroutl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TPSMessageHandler implements MQMessageHandler<DistribusjonsMelding> {

  private static final Logger logger = LoggerFactory.getLogger(TPSMessageHandler.class);

  private final AktoerregisterService aktoerregisterService;

  @Autowired
  public TPSMessageHandler(AktoerregisterService aktoerregisterService) {
    this.aktoerregisterService = aktoerregisterService;
  }

  @Override
  public boolean onMessage(DistribusjonsMelding distribusjonsMelding) {
    logger.info("Distribusjonsmelding: {}", JsonUtil.objectToJsonString(distribusjonsMelding));

    Tgironorsk giroNrNorge = distribusjonsMelding.getGiroNrNorge();
    Tgiroutl giroNrUtland = distribusjonsMelding.getGiroNrUtland();
    TAnnullering annullertGiroNrNorge = distribusjonsMelding.getAnnullertGiroNrNorge();
    TAnnullering annullertGiroNrUtland = distribusjonsMelding.getAnnullertGiroNrUtland();

    TAnnullering annulertKontonummer = annullertGiroNrNorge != null ? annullertGiroNrNorge : annullertGiroNrUtland;

    if (giroNrNorge != null) {
      logger.info("Distribusjonsmelding er av type GiroNrNorge");
      String fnr = giroNrNorge.getFnr();
      AktoerDTO aktoerDTO = aktoerregisterService.hentAktoerFromDB(fnr);
      if (aktoerDTO != null) {
        KontonummerDTO kontonummer = new KontonummerDTO();
        kontonummer.setNorskKontonr(giroNrNorge.getGiroNr());
        aktoerDTO.setKontonummer(kontonummer);
        aktoerregisterService.oppdaterAktoer(aktoerDTO);
      }
    }
    else if (giroNrUtland != null) {
      logger.info("Distribusjonsmelding er av type GiroNrUtland");
      String fnr = giroNrUtland.getFnr();
      AktoerDTO aktoerDTO = aktoerregisterService.hentAktoerFromDB(fnr);
      if (aktoerDTO != null) {
        KontonummerDTO kontonummer = new KontonummerDTO();
        kontonummer.setIban(giroNrUtland.getGiroNr());
        kontonummer.setSwift(giroNrUtland.getSwiftKode());
        kontonummer.setValutaKode(giroNrUtland.getValutaKode());
        kontonummer.setBankNavn(giroNrUtland.getBankNavn());
        kontonummer.setBankLandkode(giroNrUtland.getLandKode());
        aktoerDTO.setKontonummer(kontonummer);
        aktoerregisterService.oppdaterAktoer(aktoerDTO);
      }
    }
    else if (annulertKontonummer != null) {
      String fnr = annulertKontonummer.getFnr();
      logger.info("Kontonummer er annulert for fnr: {}", fnr);
      AktoerDTO aktoerDTO = aktoerregisterService.hentAktoerFromDB(fnr);
      if (aktoerDTO != null) {
        aktoerDTO.setKontonummer(null);
      }
    }
    return true;
  }
}

package no.nav.bidrag.aktoerregister.service.mq;

import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;
import no.nav.bidrag.aktoerregister.persistence.entities.Kontonummer;
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
import org.springframework.transaction.annotation.Transactional;

@Component
public class TPSMessageHandler implements MQMessageHandler<DistribusjonsMelding> {

  private static final Logger logger = LoggerFactory.getLogger(TPSMessageHandler.class);

  private final AktoerregisterService aktoerregisterService;

  @Autowired
  public TPSMessageHandler(AktoerregisterService aktoerregisterService) {
    this.aktoerregisterService = aktoerregisterService;
  }

  @Transactional
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
      Aktoer aktoer = aktoerregisterService.hentAktoerFromDB(fnr);
      if (aktoer != null) {
        Kontonummer kontonummer = new Kontonummer();
        kontonummer.setNorskKontonr(giroNrNorge.getGiroNr());
        aktoer.setKontonummer(kontonummer);
        aktoerregisterService.oppdaterAktoer(aktoer);
      }
    }
    else if (giroNrUtland != null) {
      logger.info("Distribusjonsmelding er av type GiroNrUtland");
      String fnr = giroNrUtland.getFnr();
      Aktoer aktoer = aktoerregisterService.hentAktoerFromDB(fnr);
      if (aktoer != null) {
        Kontonummer kontonummer = new Kontonummer();
        kontonummer.setIban(giroNrUtland.getGiroNr());
        kontonummer.setSwift(giroNrUtland.getSwiftKode());
        kontonummer.setValutaKode(giroNrUtland.getValutaKode());
        kontonummer.setBankNavn(giroNrUtland.getBankNavn());
        kontonummer.setBankLandkode(giroNrUtland.getLandKode());
        aktoer.setKontonummer(kontonummer);
        aktoerregisterService.oppdaterAktoer(aktoer);
      }
    }
    else if (annulertKontonummer != null) {
      String fnr = annulertKontonummer.getFnr();
      logger.info("Kontonummer er annulert for fnr: {}", fnr);
      Aktoer aktoer = aktoerregisterService.hentAktoerFromDB(fnr);
      if (aktoer != null) {
        aktoer.setKontonummer(null);
      }
    }
    return true;
  }
}

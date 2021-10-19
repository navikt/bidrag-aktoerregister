package no.nav.bidrag.aktoerregister.service.mq;

import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;
import no.nav.bidrag.aktoerregister.persistence.entities.Kontonummer;
import no.nav.bidrag.aktoerregister.service.AktoerregisterService;
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
  public void onMessage(DistribusjonsMelding distribusjonsMelding) {

    Tgironorsk giroNrNorge = distribusjonsMelding.getGiroNrNorge();
    Tgiroutl giroNrUtland = distribusjonsMelding.getGiroNrUtland();
    TAnnullering annullertGiroNrNorge = distribusjonsMelding.getAnnullertGiroNrNorge();
    TAnnullering annullertGiroNrUtland = distribusjonsMelding.getAnnullertGiroNrUtland();

    TAnnullering annulertKontonummer = annullertGiroNrNorge != null ? annullertGiroNrNorge : annullertGiroNrUtland;

    if (giroNrNorge != null) {
      String fnr = giroNrNorge.getFnr();
      Aktoer aktoer = hentExistingAktoer(fnr);
      if (aktoer != null) {
        Kontonummer kontonummer = new Kontonummer();
        kontonummer.setNorskKontonr(giroNrNorge.getGiroNr());
        aktoer.setKontonummer(kontonummer);
        aktoerregisterService.oppdaterAktoer(aktoer);
        logger.info("Norsk kontonummer er oppdatert for aktoer {}", fnr);
      }
    }
    else if (giroNrUtland != null) {
      String fnr = giroNrUtland.getFnr();
      Aktoer aktoer = hentExistingAktoer(fnr);
      if (aktoer != null) {
        Kontonummer kontonummer = new Kontonummer();
        kontonummer.setIban(giroNrUtland.getGiroNr());
        kontonummer.setSwift(giroNrUtland.getSwiftKode());
        kontonummer.setValutaKode(giroNrUtland.getValutaKode());
        kontonummer.setBankNavn(giroNrUtland.getBankNavn());
        kontonummer.setBankLandkode(giroNrUtland.getLandKode());
        aktoer.setKontonummer(kontonummer);
        aktoerregisterService.oppdaterAktoer(aktoer);
        logger.info("Utenlandsk kontonummer er oppdatert for aktoer {}", fnr);
      }
    }
    else if (annulertKontonummer != null) {
      String fnr = annulertKontonummer.getFnr();
      Aktoer aktoer = hentExistingAktoer(fnr);
      if (aktoer != null) {
        aktoer.setKontonummer(null);
        aktoerregisterService.oppdaterAktoer(aktoer);
        logger.info("Kontonummer er annulert for aktoer: {}", fnr);
      }
    }
  }

  private Aktoer hentExistingAktoer(String fnr) {
    Aktoer aktoer = aktoerregisterService.hentAktoerFromDB(fnr);
    if (aktoer != null) {
      logger.info("Mottatt distribusjonsmelding gjelder lagret aktoer {}", fnr);
    }
    return aktoer;
  }
}

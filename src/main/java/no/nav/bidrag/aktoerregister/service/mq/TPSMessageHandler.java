package no.nav.bidrag.aktoerregister.service.mq;

import no.nav.bidrag.aktoerregister.domene.Kontonummer;
import no.nav.bidrag.aktoerregister.util.JsonUtil;
import no.rtv.namespacetps.DistribusjonsMelding;
import no.rtv.namespacetps.TAnnullering;
import no.rtv.namespacetps.Tgironorsk;
import no.rtv.namespacetps.Tgiroutl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TPSMessageHandler implements MQMessageHandler<DistribusjonsMelding> {

  private static final Logger logger = LoggerFactory.getLogger(TPSMessageHandler.class);

  @Override
  public boolean onMessage(DistribusjonsMelding distribusjonsMelding) {
    logger.info("Distribusjonsmelding: {}", JsonUtil.objectToJsonString(distribusjonsMelding));

    Tgironorsk giroNrNorge = distribusjonsMelding.getGiroNrNorge();
    Tgiroutl giroNrUtland = distribusjonsMelding.getGiroNrUtland();
    TAnnullering annullertGiroNrNorge = distribusjonsMelding.getAnnullertGiroNrNorge();
    TAnnullering annullertGiroNrUtland = distribusjonsMelding.getAnnullertGiroNrUtland();

    if (giroNrNorge != null) {
      logger.info("Distribusjonsmelding er av type GiroNrNorge");
      String fnr = giroNrNorge.getFnr();
      Kontonummer kontonummer = new Kontonummer();
      kontonummer.setNorskKontonr(giroNrNorge.getGiroNr());
      //TODO: upsert kontonummer related to fnr
    }
    else if (giroNrUtland != null) {
      logger.info("Distribusjonsmelding er av type GiroNrUtland");
      String fnr = giroNrUtland.getFnr();
      Kontonummer kontonummer = new Kontonummer();
      kontonummer.setIban(giroNrUtland.getGiroNr());
      kontonummer.setSwift(giroNrUtland.getSwiftKode());
      kontonummer.setValutaKode(giroNrUtland.getValutaKode());
      kontonummer.setBankNavn(giroNrUtland.getBankNavn());
      kontonummer.setBankLandkode(giroNrUtland.getLandKode());
      //TODO: upsert kontonummer related to fnr
    }
    else if (annullertGiroNrNorge != null) {
      logger.info("Distribusjonsmelding er av type AnnulertGiroNrNorge");
      String fnr = annullertGiroNrNorge.getFnr();
      //TODO: delete kontonummer related to fnr
    }
    else if (annullertGiroNrUtland != null) {
      logger.info("Distribusjonsmelding er av type AnnulertGiroNrUtland");
      String fnr = annullertGiroNrUtland.getFnr();
      //TODO: delete kontonummer related to fnr
    }
    return true;
  }
}

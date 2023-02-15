package no.nav.bidrag.aktoerregister.service.mq;

import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;
import no.nav.bidrag.aktoerregister.service.AktoerregisterService;
import no.rtv.namespacetps.DistribusjonsMelding;
import no.rtv.namespacetps.TAnnullering;
import no.rtv.namespacetps.Tgironorsk;
import no.rtv.namespacetps.Tgiroutl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TPSMessageHandler implements MQMessageHandler<DistribusjonsMelding> {

  private final AktoerregisterService aktoerregisterService;

  @Autowired
  public TPSMessageHandler(AktoerregisterService aktoerregisterService) {
    this.aktoerregisterService = aktoerregisterService;
  }

  @Override
  public void onMessage(DistribusjonsMelding distribusjonsMelding) {
    if (distribusjonsMelding.getGiroNrNorge() != null) {
      oppdaterAktoerMedNorsktKontonummer(distribusjonsMelding.getGiroNrNorge());
    } else if (distribusjonsMelding.getGiroNrUtland() != null) {
      oppdaterAktoerMedUtenlandskKontonummer(distribusjonsMelding.getGiroNrUtland());
    } else if (hentAnnulertKontonummer(distribusjonsMelding) != null) {
      oppdaterAktoerMedAnnulertKontonummer(hentAnnulertKontonummer(distribusjonsMelding));
    }
  }

  private void oppdaterAktoerMedNorsktKontonummer(Tgironorsk tgironorsk) {
    Aktoer aktoer = hentExistingAktoer(tgironorsk.getFnr());
    if (aktoer != null) {
      aktoer.setNorskKontonr(tgironorsk.getGiroNr());
      aktoerregisterService.oppdaterAktoer(aktoer);
    }
  }

  private void oppdaterAktoerMedUtenlandskKontonummer(Tgiroutl giroUtland) {
    Aktoer aktoer = hentExistingAktoer(giroUtland.getFnr());
    if (aktoer != null) {
      aktoer.setIban(giroUtland.getGiroNr());
      aktoer.setSwift(giroUtland.getSwiftKode());
      aktoer.setValutaKode(giroUtland.getValutaKode());
      aktoer.setBankNavn(giroUtland.getBankNavn());
      aktoer.setBankCode(giroUtland.getBankKode());
      aktoer.setBankLandkode(giroUtland.getLandKode());
      aktoerregisterService.oppdaterAktoer(aktoer);
    }
  }

  private void oppdaterAktoerMedAnnulertKontonummer(TAnnullering annulertKontonummer) {
    String fnr = annulertKontonummer.getFnr();
    Aktoer aktoer = hentExistingAktoer(fnr);
    if (aktoer != null) {
      aktoer.setNorskKontonr(null);
      aktoer.setIban(null);
      aktoer.setSwift(null);
      aktoer.setValutaKode(null);
      aktoer.setBankNavn(null);
      aktoer.setBankCode(null);
      aktoer.setBankLandkode(null);
      aktoerregisterService.oppdaterAktoer(aktoer);
    }
  }

  private TAnnullering hentAnnulertKontonummer(DistribusjonsMelding distribusjonsMelding) {
    return distribusjonsMelding.getAnnullertGiroNrNorge() != null
        ? distribusjonsMelding.getAnnullertGiroNrNorge()
        : distribusjonsMelding.getAnnullertGiroNrUtland();
  }

  private Aktoer hentExistingAktoer(String fnr) {
    return aktoerregisterService.hentAktoerFromDB(fnr);
  }
}

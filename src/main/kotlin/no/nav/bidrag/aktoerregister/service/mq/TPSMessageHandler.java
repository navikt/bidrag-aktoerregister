package no.nav.bidrag.aktoerregister.service.mq;

import javax.transaction.Transactional;
import no.nav.bidrag.aktoerregister.persistence.entities.Aktør;
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
  @Transactional
  public void onMessage(DistribusjonsMelding distribusjonsMelding) {
    if (distribusjonsMelding.getGiroNrNorge() != null) { //TODO() Må kalle TPS fremfor å bruke distrubisjonsmeldingen som vi mottar.
      oppdaterAktoerMedNorsktKontonummer(distribusjonsMelding.getGiroNrNorge());
    } else if (distribusjonsMelding.getGiroNrUtland() != null) {
      oppdaterAktoerMedUtenlandskKontonummer(distribusjonsMelding.getGiroNrUtland());
    } else if (hentAnnulertKontonummer(distribusjonsMelding) != null) {
      oppdaterAktoerMedAnnulertKontonummer(hentAnnulertKontonummer(distribusjonsMelding));
    }
  }

  private void oppdaterAktoerMedNorsktKontonummer(Tgironorsk tgironorsk) {
    Aktør aktør = hentExistingAktoer(tgironorsk.getFnr());
    if (aktør != null) {
      aktør.setNorskKontonr(tgironorsk.getGiroNr());
      aktoerregisterService.oppdaterAktoer(aktør);
    }
  }

  private void oppdaterAktoerMedUtenlandskKontonummer(Tgiroutl giroUtland) {
    Aktør aktør = hentExistingAktoer(giroUtland.getFnr());
    if (aktør != null) {
      aktør.setIban(giroUtland.getGiroNr());
      aktør.setSwift(giroUtland.getSwiftKode());
      aktør.setValutaKode(giroUtland.getValutaKode());
      aktør.setBankNavn(giroUtland.getBankNavn());
      aktør.setBankCode(giroUtland.getBankKode());
      aktør.setBankLandkode(giroUtland.getLandKode());
      aktoerregisterService.oppdaterAktoer(aktør);
    }
  }

  private void oppdaterAktoerMedAnnulertKontonummer(TAnnullering annulertKontonummer) {
    String fnr = annulertKontonummer.getFnr();
    Aktør aktør = hentExistingAktoer(fnr);
    if (aktør != null) {
      aktør.setNorskKontonr(null);
      aktør.setIban(null);
      aktør.setSwift(null);
      aktør.setValutaKode(null);
      aktør.setBankNavn(null);
      aktør.setBankCode(null);
      aktør.setBankLandkode(null);
      aktoerregisterService.oppdaterAktoer(aktør);
    }
  }

  private TAnnullering hentAnnulertKontonummer(DistribusjonsMelding distribusjonsMelding) {
    return distribusjonsMelding.getAnnullertGiroNrNorge() != null
        ? distribusjonsMelding.getAnnullertGiroNrNorge()
        : distribusjonsMelding.getAnnullertGiroNrUtland();
  }

  private Aktør hentExistingAktoer(String fnr) {
    return aktoerregisterService.hentAktørFraDatabase(fnr);
  }
}

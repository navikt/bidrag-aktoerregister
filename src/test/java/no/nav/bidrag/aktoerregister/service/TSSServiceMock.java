package no.nav.bidrag.aktoerregister.service;

import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;

public class TSSServiceMock implements AktoerService {

  @Override
  public Aktoer hentAktoer(String aktoerIdent) {
    return Aktoer.builder()
        .aktoerIdent(aktoerIdent)
        .aktoerType("AKTOERNUMMER")
        .adresselinje1("Testgate 1")
        .build();
  }
}

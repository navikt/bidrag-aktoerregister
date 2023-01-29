package no.nav.bidrag.aktoerregister.repository;

import java.util.HashMap;
import java.util.Map;
import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;
import no.nav.bidrag.aktoerregister.persistence.entities.Hendelse;

public class MockDB {
  public final Map<String, Aktoer> aktoerMap;

  public final Map<Integer, Hendelse> hendelseMap;

  public MockDB() {
    aktoerMap = new HashMap<>();
    hendelseMap = new HashMap<>();
  }
}

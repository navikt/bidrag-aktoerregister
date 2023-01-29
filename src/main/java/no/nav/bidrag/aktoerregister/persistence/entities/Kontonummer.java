package no.nav.bidrag.aktoerregister.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Kontonummer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private int id;

  @Column(name = "norskkontonr")
  private String norskKontonr;

  @Column(name = "iban")
  private String iban;

  @Column(name = "swift")
  private String swift;

  @Column(name = "banknavn")
  private String bankNavn;

  @Column(name = "banklandkode")
  private String bankLandkode;

  @Column(name = "bankcode")
  private String bankCode;

  @Column(name = "valutakode")
  private String valutaKode;
}

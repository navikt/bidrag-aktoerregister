package no.nav.bidrag.aktoerregister.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Hendelse {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "sekvensnummer")
  private int sekvensnummer;

  @ManyToOne
  @JoinColumn(name = "aktoer_id", referencedColumnName = "id")
  private Aktoer aktoer;

  @Column(name = "aktoer_ident")
  private String aktoerIdent;

  public Hendelse() {
  }

  public Hendelse(Aktoer aktoer) {
    this.aktoer = aktoer;
    this.aktoerIdent = aktoer.getAktoerIdent();
  }

  public Hendelse(int sekvensnummer, Aktoer aktoer) {
    this.sekvensnummer = sekvensnummer;
    this.aktoer = aktoer;
  }
}

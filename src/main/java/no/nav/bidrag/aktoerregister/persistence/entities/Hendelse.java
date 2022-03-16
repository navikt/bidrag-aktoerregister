package no.nav.bidrag.aktoerregister.persistence.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
public class Hendelse {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "sekvensnummer")
  private int sekvensnummer;

  @ManyToOne
  @JoinColumn(name = "aktoerid", referencedColumnName = "aktoerid")
  private Aktoer aktoer;

  public Hendelse() {
  }

  public Hendelse(Aktoer aktoer) {
    this.aktoer = aktoer;
  }

  public Hendelse(int sekvensnummer, Aktoer aktoer) {
    this.sekvensnummer = sekvensnummer;
    this.aktoer = aktoer;
  }
}

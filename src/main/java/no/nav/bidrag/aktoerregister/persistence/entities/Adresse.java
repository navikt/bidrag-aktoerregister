package no.nav.bidrag.aktoerregister.persistence.entities;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Adresse {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private int id;

  @Column(name = "navn")
  private String navn;

  @Column(name = "adresselinje1")
  private String adresselinje1;

  @Column(name = "adresselinje2")
  private String adresselinje2;

  @Column(name = "adresselinje3")
  private String adresselinje3;

  @Column(name = "postnr")
  private String postnr;

  @Column(name = "poststed")
  private String poststed;

  @Column(name = "land")
  private String land;

  @Version
  @Column(name = "sist_endret")
  private Timestamp sistEndret;
}

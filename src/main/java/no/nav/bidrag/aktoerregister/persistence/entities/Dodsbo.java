package no.nav.bidrag.aktoerregister.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity(name = "dodsbo")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dodsbo {

  @Id
  @Column(name = "id")
  @EqualsAndHashCode.Exclude
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "kontaktperson")
  private String kontaktperson;

  @Column(name = "adresselinje1")
  private String adresselinje1;

  @Column(name = "adresselinje2")
  private String adresselinje2;

  @Column(name = "adresselinje3")
  private String adresselinje3;

  @Column(name = "leilighetsnummer")
  private String leilighetsnummer;

  @Column(name = "postnr")
  private String postnr;

  @Column(name = "poststed")
  private String poststed;

  @Column(name = "land")
  private String land;

  @OneToOne
  @JoinColumn(name = "aktoer_id")
  private Aktoer aktoer;
}

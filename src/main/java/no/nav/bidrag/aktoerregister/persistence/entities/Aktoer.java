package no.nav.bidrag.aktoerregister.persistence.entities;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class Aktoer {

  @Id
  @Column(name = "id")
  @EqualsAndHashCode.Exclude
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "aktoer_ident")
  private String aktoerIdent;

  @Column(name = "aktoertype")
  private String aktoerType;

  @Column(name = "offentlig_id")
  private String offentligId;

  @Column(name = "offentlig_id_type")
  private String offentligIdType;

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

  @Builder.Default
  @EqualsAndHashCode.Exclude
  @OneToMany(mappedBy = "aktoer", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Hendelse> hendelser = new ArrayList<>();

  @Version
  @Column(name = "sist_endret")
  @EqualsAndHashCode.Exclude
  private Timestamp sistEndret;

  public void addHendelse(Hendelse hendelse) {
    hendelser.add(hendelse);
  }
}

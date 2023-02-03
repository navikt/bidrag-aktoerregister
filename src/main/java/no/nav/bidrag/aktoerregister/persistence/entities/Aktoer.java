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
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Version;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Aktoer {

  @Id
  @Column(name = "id")
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

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "adresseid", referencedColumnName = "id")
  private Adresse adresse;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "kontonummerid", referencedColumnName = "id")
  private Kontonummer kontonummer;

  @OneToMany(mappedBy = "aktoer", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Hendelse> hendelser = new ArrayList<>();

  @Version
  @Column(name = "sist_endret")
  private Timestamp sistEndret;

  public void addHendelse(Hendelse hendelse) {
    hendelser.add(hendelse);
  }
}

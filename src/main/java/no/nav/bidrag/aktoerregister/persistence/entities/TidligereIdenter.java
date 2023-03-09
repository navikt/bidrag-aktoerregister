package no.nav.bidrag.aktoerregister.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity(name = "tidligere_identer")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TidligereIdenter {

  @Id
  @Column(name = "id")
  @EqualsAndHashCode.Exclude
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "tidligere_aktoer_ident")
  private String tidligereAktoerIdent;

  @Column(name = "identtype")
  private String identtype;

  @ManyToOne
  @JoinColumn(name = "aktoer_id")
  private Aktoer aktoer;
}

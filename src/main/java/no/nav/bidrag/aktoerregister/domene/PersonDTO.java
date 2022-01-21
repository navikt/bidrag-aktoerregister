package no.nav.bidrag.aktoerregister.domene;

import java.util.List;
import lombok.Data;

@Data
public class PersonDTO {
  private List<BostedsadresseDTO> bostedsadresse;
  private List<UtenlandskAdresseDTO> utenlandskAdresse;

  @Data
  public static class BostedsadresseDTO {
    private String coAdressenavn;
    private VegadresseDTO vegadresse;
  }

  @Data
  public static class VegadresseDTO {
    private String adressenavn;
    private String husnummer;
    private String husbokstav;
    private String postnummer;
  }

  @Data
  public static class UtenlandskAdresseDTO {
    private String adressenavnNummer;
    private String bygningEtasjeLeilighet;
    private String postboksNummerNavn;
    private String postkode;
    private String bySted;
    private String regionDistriktOmraade;
    private String landkode;
  }
}

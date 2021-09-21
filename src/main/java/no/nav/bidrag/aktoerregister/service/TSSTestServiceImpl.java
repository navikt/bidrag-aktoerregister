package no.nav.bidrag.aktoerregister.service;

import jakarta.xml.bind.JAXBException;
import java.util.List;
import javax.jms.JMSException;
import no.nav.bidrag.aktoerregister.domene.Adresse;
import no.nav.bidrag.aktoerregister.domene.Aktoer;
import no.nav.bidrag.aktoerregister.domene.AktoerId;
import no.nav.bidrag.aktoerregister.domene.Identtype;
import no.nav.bidrag.aktoerregister.domene.Kontonummer;
import no.nav.bidrag.aktoerregister.properties.MQProperties;
import no.rtv.namespacetss.AdresseSamhType;
import no.rtv.namespacetss.KontoType;
import no.rtv.namespacetss.ObjectFactory;
import no.rtv.namespacetss.Samhandler;
import no.rtv.namespacetss.SamhandlerIDataB910Type;
import no.rtv.namespacetss.TServicerutiner;
import no.rtv.namespacetss.TidOFF1;
import no.rtv.namespacetss.TssSamhandlerData;
import no.rtv.namespacetss.TssSamhandlerData.TssInputData;
import no.rtv.namespacetss.TypeOD910;
import no.rtv.namespacetss.TypeSamhAdr;
import no.rtv.namespacetss.TypeSamhKonto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TSSTestServiceImpl implements TSSTestService{

  private final MQService mqService;

  private final MQProperties mqProperties;

  @Autowired
  public TSSTestServiceImpl(MQService mqService, MQProperties mqProperties) {
    this.mqService = mqService;
    this.mqProperties = mqProperties;
  }

  @Override
  public Aktoer hentAktoer(AktoerId aktoerId) throws JAXBException, JMSException {
    TssSamhandlerData request = createTssSamhandlerRequest(aktoerId);
    TssSamhandlerData response = mqService.performRequestResponse(mqProperties.getQueueName(), request, TssSamhandlerData.class, TssSamhandlerData.class);
    return mapToAktoer(response, aktoerId);
  }

  @Override
  public TssSamhandlerData hentTssSamhandler(AktoerId aktoerId) throws JAXBException, JMSException {
    TssSamhandlerData request = createTssSamhandlerRequest(aktoerId);
    return mqService.performRequestResponse(mqProperties.getQueueName(), request, TssSamhandlerData.class, TssSamhandlerData.class);
  }

  private TssSamhandlerData createTssSamhandlerRequest(AktoerId aktoerId) {
    ObjectFactory objectFactory = new ObjectFactory();
    TServicerutiner servicerutiner = objectFactory.createTServicerutiner();

    SamhandlerIDataB910Type samhandlerIDataB910 = objectFactory.createSamhandlerIDataB910Type();
    if (aktoerId.getIdenttype().equals(Identtype.AKTOERNUMMER)) {
      samhandlerIDataB910.setIdOffTSS(aktoerId.getAktoerId());
    }
    else {
      TidOFF1 tidOFF1 = objectFactory.createTidOFF1();
      tidOFF1.setIdOff(aktoerId.getAktoerId());
      tidOFF1.setKodeIdType("FNR");
      samhandlerIDataB910.setOFFid(tidOFF1);
    }
    samhandlerIDataB910.setHistorikk("N");
    samhandlerIDataB910.setBrukerID("HMB2990");
    servicerutiner.setSamhandlerIDataB910(samhandlerIDataB910);

    TssInputData tssInputData = objectFactory.createTssSamhandlerDataTssInputData();
    tssInputData.setTssServiceRutine(servicerutiner);
    TssSamhandlerData tssSamhandlerData = objectFactory.createTssSamhandlerData();
    tssSamhandlerData.setTssInputData(tssInputData);
    return tssSamhandlerData;
  }

  private Aktoer mapToAktoer(TssSamhandlerData tssSamhandlerData, AktoerId aktoerId) {
    Aktoer aktoer = new Aktoer(aktoerId);
    TypeOD910 samhandlerODataB910 = tssSamhandlerData.getTssOutputData().getSamhandlerODataB910();
    if (samhandlerODataB910 != null) {
      aktoer.setAdresse(mapToAdresse(samhandlerODataB910));
      aktoer.setKontonummer(mapToKontonummer(samhandlerODataB910));
    }
    return aktoer;
  }

  private Adresse mapToAdresse(TypeOD910 samhandlerODataB910) {
    List<Samhandler> samhandlerListe = samhandlerODataB910.getEnkeltSamhandler();
    if (samhandlerListe.size() > 0) {
      TypeSamhAdr typeSamhAdr = samhandlerListe.get(0).getAdresse130();
      if (Integer.parseInt(typeSamhAdr.getAntAdresse()) > 0) {
        AdresseSamhType adresseSamhType = typeSamhAdr.getAdresseSamh().get(0);
        Adresse adresse = new Adresse();
        adresse.setLand(adresseSamhType.getKodeLand());
        adresse.setPoststed(adresseSamhType.getPoststed());
        adresse.setPostnr(adresseSamhType.getPostNr());
        List<String> adresselinjer = adresseSamhType.getAdrLinjeInfo().getAdresseLinje();
        if (adresselinjer.size() >= 1) {
          adresse.setAdresselinje1(adresselinjer.get(0));
        }
        if(adresselinjer.size() >= 2) {
          adresse.setAdresselinje2(adresselinjer.get(1));
        }
        if(adresselinjer.size() >= 3) {
          adresse.setAdresselinje3(adresselinjer.get(2));
        }
        return adresse;
      }
    }
    return null;
  }

  private Kontonummer mapToKontonummer(TypeOD910 samhandlerODataB910) {
    List<Samhandler> samhandlerListe = samhandlerODataB910.getEnkeltSamhandler();
    if (samhandlerListe.size() > 0) {
      TypeSamhKonto typeSamhKonto = samhandlerListe.get(0).getKonto140();
      if (Integer.parseInt(typeSamhKonto.getAntKonto()) > 0) {
        KontoType kontoType = typeSamhKonto.getKonto().get(0);
        Kontonummer kontonummer = new Kontonummer();
        kontonummer.setBankLandkode(kontoType.getKodeLand());
        kontonummer.setBankNavn(kontoType.getBankNavn());
        kontonummer.setNorskKontonr(kontoType.getGironrInnland());
        kontonummer.setSwift(kontoType.getSwiftKode());
        kontonummer.setValutaKode(kontoType.getKodeValuta());
        kontonummer.setIban(kontoType.getGironrUtland());
        return kontonummer;
      }
    }
    return null;
  }
}

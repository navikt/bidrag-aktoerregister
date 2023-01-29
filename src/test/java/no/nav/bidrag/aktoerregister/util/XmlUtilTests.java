package no.nav.bidrag.aktoerregister.util;

import static no.nav.bidrag.felles.test.data.person.TestPersonBuilder.person;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import jakarta.xml.bind.JAXBException;
import javax.jms.JMSException;
import no.nav.bidrag.felles.test.data.person.TestPerson;
import no.rtv.namespacetps.ObjectFactory;
import no.rtv.namespacetps.SRnavn;
import no.rtv.namespacetps.TpsPersonData;
import no.rtv.namespacetps.TpsPersonData.TpsServiceRutine;
import org.junit.jupiter.api.Test;

public class XmlUtilTests {

  private static TestPerson PERSON1 = person().opprett();

  @Test
  public void TestThatXMLStringIsCorrectlyMappedBackToObject() throws JAXBException, JMSException {
    ObjectFactory objectFactory = new ObjectFactory();
    TpsPersonData tpsPersonData = objectFactory.createTpsPersonData();

    TpsServiceRutine tpsServiceRutine = objectFactory.createTpsPersonDataTpsServiceRutine();
    tpsServiceRutine.setServiceRutinenavn(SRnavn.FS_03_FDNUMMER_GIRONUMR_O);
    tpsServiceRutine.setFnr(PERSON1.getPersonIdent());
    tpsServiceRutine.setAksjonsKode("A");
    tpsServiceRutine.setAksjonsKode2("0");

    tpsPersonData.setTpsServiceRutine(tpsServiceRutine);

    String xmlString = XmlUtil.createXMLString(tpsPersonData, TpsPersonData.class);
    assertNotNull(xmlString);
    assertNotEquals(xmlString, "");
    TpsPersonData objectFromXmlString =
        XmlUtil.getObjectFromXMLMessage(xmlString, TpsPersonData.class);
    assertNotNull(objectFromXmlString);
    assertEquals(
        objectFromXmlString.getTpsServiceRutine().getServiceRutinenavn(),
        tpsServiceRutine.getServiceRutinenavn());
    assertEquals(objectFromXmlString.getTpsServiceRutine().getFnr(), tpsServiceRutine.getFnr());
    assertEquals(
        objectFromXmlString.getTpsServiceRutine().getAksjonsKode(),
        tpsServiceRutine.getAksjonsKode());
    assertEquals(
        objectFromXmlString.getTpsServiceRutine().getAksjonsKode2(),
        tpsServiceRutine.getAksjonsKode2());
  }
}

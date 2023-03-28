package no.nav.bidrag.aktoerregister.util;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.transform.stream.StreamSource;

public class XmlUtil {

  public static <T> String createXMLString(T object, Class<T> objectType) throws JAXBException {
    JAXBContext jaxbContext = JAXBContext.newInstance(objectType);
    Marshaller marshaller = jaxbContext.createMarshaller();
    StringWriter stringWriter = new StringWriter();
    marshaller.marshal(object, stringWriter);
    return stringWriter.toString();
  }

  public static <T> T getObjectFromXMLMessage(String xmlMessage, Class<T> objectType)
      throws JAXBException {
    JAXBContext jaxbContext = JAXBContext.newInstance(objectType);
    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
    StreamSource streamSource = new StreamSource(new StringReader(xmlMessage));
    JAXBElement<T> jaxbElement = unmarshaller.unmarshal(streamSource, objectType);
    return jaxbElement.getValue();
  }
}

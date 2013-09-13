package org.energyos.espi.datacustodian.domain;

import com.sun.syndication.io.FeedException;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.energyos.espi.datacustodian.atom.XMLTest;
import org.energyos.espi.datacustodian.utils.EspiMarshaller;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlTransient;
import java.io.IOException;

import static org.energyos.espi.datacustodian.Asserts.assertXpathValue;
import static org.energyos.espi.datacustodian.support.TestUtils.assertAnnotationPresent;
import static org.energyos.espi.datacustodian.utils.factories.EspiFactory.newIntervalBlock;

public class IntervalBlockTests extends XMLTest {

    private  String xml;

    @Before
    public void before() throws JAXBException, FeedException {
        xml = EspiMarshaller.marshal(newIntervalBlock());
    }

    @Test
    public void intervalDuration() throws SAXException, IOException, XpathException {
        assertXpathValue("86400", "IntervalBlock/interval/duration", xml);
    }

    @Test
    public void intervalStart() throws SAXException, IOException, XpathException {
        assertXpathValue("1330578000", "IntervalBlock/interval/start", xml);
    }

    @Test
    public void meterReading_hasTransientAnnotation() {
        assertAnnotationPresent(IntervalBlock.class, "meterReading", XmlTransient.class);
    }
}

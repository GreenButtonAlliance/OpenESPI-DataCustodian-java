package org.energyos.espi.datacustodian.domain;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.energyos.espi.datacustodian.atom.XMLTest;
import org.energyos.espi.datacustodian.models.atom.adapters.IntervalBlockAdapter;
import org.energyos.espi.datacustodian.utils.EspiMarshaller;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.IOException;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathExists;
import static org.energyos.espi.datacustodian.support.Asserts.assertXpathValue;
import static org.energyos.espi.datacustodian.support.TestUtils.assertAnnotationPresent;
import static org.energyos.espi.datacustodian.utils.factories.EspiFactory.newIntervalBlockWithUsagePoint;
import static org.junit.Assert.assertEquals;

public class IntervalBlockTests extends XMLTest {
    static final String XML_INPUT =
            "<IntervalBlock xmlns=\"http://naesb.org/espi\">" +
                "<interval>" +
                    "<duration>3</duration>" +
                    "<start>4</start>" +
                "</interval>" +
                "<IntervalReading></IntervalReading>" +
                "<IntervalReading></IntervalReading>" +
            "</IntervalBlock>";

    private IntervalBlock intervalBlock;
    private  String xml;

    @Before
    public void before() throws Exception {
        xml = EspiMarshaller.marshal(newIntervalBlockWithUsagePoint());

        IntervalBlockAdapter intervalBlockAdapter = new IntervalBlockAdapter();
        JAXBElement<IntervalBlock> intervalBlockJAXBElement = EspiMarshaller.unmarshal(XML_INPUT);
        intervalBlock = intervalBlockAdapter.unmarshal(intervalBlockJAXBElement);
    }

    @Test
    public void unmarshallsIntervalBlock() {
        assertEquals(IntervalBlock.class, intervalBlock.getClass());
    }

    @Test
    public void unmarshal_setsInterval() {
        assertEquals(3L, intervalBlock.getInterval().getDuration().longValue());
        assertEquals(4L, intervalBlock.getInterval().getStart().longValue());
    }

    @Test
    public void unmarshal_setsIntervalReadings() {
        assertEquals(intervalBlock, intervalBlock.getIntervalReadings().get(0).getIntervalBlock());
        assertEquals(intervalBlock, intervalBlock.getIntervalReadings().get(1).getIntervalBlock());
    }

    @Test
    public void marshall_setsIntervalDuration() throws SAXException, IOException, XpathException {
        assertXpathValue("86400", "IntervalBlock/interval/duration", xml);
    }

    @Test
    public void marshall_setsIntervalStart() throws SAXException, IOException, XpathException {
        assertXpathValue("1330578000", "IntervalBlock/interval/start", xml);
    }

    @Test
    public void marshall_setsIntervalReadings() throws SAXException, IOException, XpathException {
        assertXpathExists("IntervalBlock/IntervalReading[1]", xml);
        assertXpathExists("IntervalBlock/IntervalReading[2]", xml);
    }

    @Test
    public void meterReading_hasTransientAnnotation() {
        assertAnnotationPresent(IntervalBlock.class, "meterReading", XmlTransient.class);
    }
}

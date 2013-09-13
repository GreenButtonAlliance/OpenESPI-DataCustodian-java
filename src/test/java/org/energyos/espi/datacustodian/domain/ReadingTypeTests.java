package org.energyos.espi.datacustodian.domain;

import com.sun.syndication.io.FeedException;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.energyos.espi.datacustodian.atom.XMLTest;
import org.energyos.espi.datacustodian.utils.EspiMarshaller;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.math.BigInteger;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathExists;
import static org.energyos.espi.datacustodian.Asserts.assertXpathValue;
import static org.energyos.espi.datacustodian.utils.factories.EspiFactory.newReadingType;

public class ReadingTypeTests extends XMLTest {

    private String xml;

    @Before
    public void before() throws JAXBException, FeedException {
        xml = EspiMarshaller.marshal(newReadingType());
    }

    @Test
    public void readingType() throws SAXException, IOException, XpathException {
        assertXpathExists("ReadingType", xml);
    }

    @Test
    public void accumulationBehaviour() throws SAXException, IOException, XpathException {
        assertXpathValue("accumulationBehaviour", "ReadingType/accumulationBehaviour", xml);
    }

    @Test
    public void commodity() throws SAXException, IOException, XpathException {
        assertXpathValue("commodity", "ReadingType/commodity", xml);
    }

    @Test
    public void dataQualifier() throws SAXException, IOException, XpathException {
        assertXpathValue("dataQualifier", "ReadingType/dataQualifier", xml);
    }

    @Test
    public void flowDirection() throws SAXException, IOException, XpathException {
        assertXpathValue("flowDirection", "ReadingType/flowDirection", xml);
    }

    @Test
    public void intervalLength() throws SAXException, IOException, XpathException {
        assertXpathValue("10", "ReadingType/intervalLength", xml);
    }

    @Test
    public void kind() throws SAXException, IOException, XpathException {
        assertXpathValue("kind", "ReadingType/kind", xml);
    }

    @Test
    public void phase() throws SAXException, IOException, XpathException {
        assertXpathValue("phase", "ReadingType/phase", xml);
    }

    @Test
    public void powerOfTenMultiplier() throws SAXException, IOException, XpathException {
        assertXpathValue("multiplier", "ReadingType/powerOfTenMultiplier", xml);
    }

    @Test
    public void timeAttribute() throws SAXException, IOException, XpathException {
        assertXpathValue("timeAttribute", "ReadingType/timeAttribute", xml);
    }

    @Test
    public void uom() throws SAXException, IOException, XpathException {
        assertXpathValue("uom", "ReadingType/uom", xml);
    }

    @Test
    public void consumptionTier() throws SAXException, IOException, XpathException {
        assertXpathValue("consumptionTier", "ReadingType/consumptionTier", xml);
    }

    @Test
    public void cpp() throws SAXException, IOException, XpathException {
        assertXpathValue("cpp", "ReadingType/cpp", xml);
    }

    @Test
    public void currency() throws SAXException, IOException, XpathException {
        assertXpathValue("currency", "ReadingType/currency", xml);
    }

    @Test
    public void interharmonic() throws SAXException, IOException, XpathException {
        assertXpathValue("1", "ReadingType/interharmonic/numerator", xml);
        assertXpathValue("6", "ReadingType/interharmonic/denominator", xml);
    }

    @Test
    public void measuringPeriod() throws SAXException, IOException, XpathException {
        assertXpathValue("measuringPeriod", "ReadingType/measuringPeriod", xml);
    }

    @Test
    public void tou() throws SAXException, IOException, XpathException {
        assertXpathValue("tou", "ReadingType/tou", xml);
    }

    @Test
    public void aggregate() throws SAXException, IOException, XpathException {
        assertXpathValue("aggregate", "ReadingType/aggregate", xml);
    }

    @Test
    public void argument() throws SAXException, IOException, XpathException {
        RationalNumber number = new RationalNumber();
        number.setNumerator(new BigInteger("1"));
        number.setDenominator(new BigInteger("3"));

        assertXpathValue("1", "ReadingType/argument/numerator", xml);
        assertXpathValue("3", "ReadingType/argument/denominator", xml);
    }
}

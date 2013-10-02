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
import static org.energyos.espi.datacustodian.support.Asserts.assertXpathValue;
import static org.energyos.espi.datacustodian.utils.factories.EspiFactory.newReadingType;
import static org.junit.Assert.assertEquals;

public class ReadingTypeTests extends XMLTest {

    static final String XML_INPUT =
            "<ReadingType xmlns=\"http://naesb.org/espi\">" +
                "<accumulationBehaviour>Behaviour</accumulationBehaviour>" +
                "<commodity>Commodity</commodity>" +
                "<dataQualifier>DataQualifier</dataQualifier>" +
                "<flowDirection>FlowDirection</flowDirection>" +
                "<intervalLength>1</intervalLength>" +
                "<kind>Kind</kind>" +
                "<phase>Phase</phase>" +
                "<powerOfTenMultiplier>PowerOfTenMultiplier</powerOfTenMultiplier>" +
                "<timeAttribute>TimeAttribute</timeAttribute>" +
                "<uom>Uom</uom>" +
                "<consumptionTier>ConsumptionTier</consumptionTier>" +
                "<cpp>Cpp</cpp>" +
                "<currency>Currency</currency>" +
                "<interharmonic>" +
                    "<numerator>1</numerator>" +
                    "<denominator>2</denominator>" +
                "</interharmonic>" +
                "<measuringPeriod>MeasuringPeriod</measuringPeriod>" +
                "<tou>Tou</tou>" +
                "<aggregate>Aggregate</aggregate>" +
                "<argument>"+
                    "<numerator>1</numerator>" +
                    "<denominator>3</denominator>" +
                "</argument>" +
            "</ReadingType>";

    private ReadingType readingType;
    private String xml;

    @Before
    public void before() throws JAXBException, FeedException {
        xml = EspiMarshaller.marshal(newReadingType());
        readingType = EspiMarshaller.<ReadingType>unmarshal(XML_INPUT).getValue();
    }

    @Test
    public void unmarshalsReadingType() {
        assertEquals(ReadingType.class, readingType.getClass());
    }

    @Test
    public void unmarshal_setsAccumulationBehaviour() {
        assertEquals("Behaviour",readingType.getAccumulationBehaviour());
    }

    @Test
    public void unmarshal_setsCommodity() {
        assertEquals("Commodity", readingType.getCommodity());
    }

    @Test
    public void unmarshal_setsDataQualifier() {
        assertEquals("DataQualifier", readingType.getDataQualifier());
    }

    @Test
    public void unmarshal_setsFlowDirection() {
        assertEquals("FlowDirection", readingType.getFlowDirection());
    }

    @Test
    public void unmarshal_setsIntervalLength() {
        assertEquals(new Long(1L), readingType.getIntervalLength());
    }

    @Test
    public void unmarshal_setsKind() {
        assertEquals("Kind", readingType.getKind());
    }

    @Test
    public void unmarshal_setsPhase() {
        assertEquals("Phase", readingType.getPhase());
    }

    @Test
    public void unmarshal_setsPowerOfTenMultiplier() {
        assertEquals("PowerOfTenMultiplier", readingType.getPowerOfTenMultiplier());
    }

    @Test
    public void unmarshal_setsTimeAttribute() {
        assertEquals("TimeAttribute", readingType.getTimeAttribute());
    }

    @Test
    public void unmarshal_setsUom() {
        assertEquals("Uom", readingType.getUom());
    }

    @Test
    public void unmarshal_setsConsumptionTier() {
        assertEquals("ConsumptionTier", readingType.getConsumptionTier());
    }

    @Test
    public void unmarshal_setsCpp() {
        assertEquals("Cpp", readingType.getCpp());
    }

    @Test
    public void unmarshal_setsCurrency() {
        assertEquals("Currency", readingType.getCurrency());
    }

    @Test
    public void unmarshal_setsInterharmonic() {
        ReadingInterharmonic interharmonic = new ReadingInterharmonic();
        interharmonic.setNumerator(new BigInteger("1"));
        interharmonic.setDenominator(new BigInteger("2"));

        assertEquals(interharmonic.getNumerator(), readingType.getInterharmonic().getNumerator());
        assertEquals(interharmonic.getDenominator(), readingType.getInterharmonic().getDenominator());
    }

    @Test
    public void unmarshal_setsMeasuringPeriod() {
        assertEquals("MeasuringPeriod", readingType.getMeasuringPeriod());
    }

    @Test
    public void unmarshal_setsTou() {
        assertEquals("Tou", readingType.getTou());
    }

    @Test
    public void unmarshal_setsAggregate() {
        assertEquals("Aggregate", readingType.getAggregate());
    }

    @Test
    public void unmarshal_setsArgument() {
        RationalNumber number = new RationalNumber();
        number.setNumerator(new BigInteger("1"));
        number.setDenominator(new BigInteger("3"));

        assertEquals(number.getNumerator(), readingType.getArgument().getNumerator());
        assertEquals(number.getDenominator(), readingType.getArgument().getDenominator());
    }

    @Test
    public void marshalsReadingType() throws SAXException, IOException, XpathException {
        assertXpathExists("ReadingType", xml);
    }

    @Test
    public void marshal_setsAccumulationBehaviour() throws SAXException, IOException, XpathException {
        assertXpathValue("accumulationBehaviour", "ReadingType/accumulationBehaviour", xml);
    }

    @Test
    public void marshal_setsCommodity() throws SAXException, IOException, XpathException {
        assertXpathValue("commodity", "ReadingType/commodity", xml);
    }

    @Test
    public void marshal_setsDataQualifier() throws SAXException, IOException, XpathException {
        assertXpathValue("dataQualifier", "ReadingType/dataQualifier", xml);
    }

    @Test
    public void marshal_setsFlowDirection() throws SAXException, IOException, XpathException {
        assertXpathValue("flowDirection", "ReadingType/flowDirection", xml);
    }

    @Test
    public void marshal_setsIntervalLength() throws SAXException, IOException, XpathException {
        assertXpathValue("10", "ReadingType/intervalLength", xml);
    }

    @Test
    public void marshal_setsKind() throws SAXException, IOException, XpathException {
        assertXpathValue("kind", "ReadingType/kind", xml);
    }

    @Test
    public void marshal_setsPhase() throws SAXException, IOException, XpathException {
        assertXpathValue("phase", "ReadingType/phase", xml);
    }

    @Test
    public void marshal_setsPowerOfTenMultiplier() throws SAXException, IOException, XpathException {
        assertXpathValue("multiplier", "ReadingType/powerOfTenMultiplier", xml);
    }

    @Test
    public void marshal_setsTimeAttribute() throws SAXException, IOException, XpathException {
        assertXpathValue("timeAttribute", "ReadingType/timeAttribute", xml);
    }

    @Test
    public void marshal_setsUom() throws SAXException, IOException, XpathException {
        assertXpathValue("uom", "ReadingType/uom", xml);
    }

    @Test
    public void marshal_setsConsumptionTier() throws SAXException, IOException, XpathException {
        assertXpathValue("consumptionTier", "ReadingType/consumptionTier", xml);
    }

    @Test
    public void marshal_setsCpp() throws SAXException, IOException, XpathException {
        assertXpathValue("cpp", "ReadingType/cpp", xml);
    }

    @Test
    public void marshal_setsCurrency() throws SAXException, IOException, XpathException {
        assertXpathValue("currency", "ReadingType/currency", xml);
    }

    @Test
    public void marshal_setsInterharmonic() throws SAXException, IOException, XpathException {
        assertXpathValue("1", "ReadingType/interharmonic/numerator", xml);
        assertXpathValue("6", "ReadingType/interharmonic/denominator", xml);
    }

    @Test
    public void marshal_setsMeasuringPeriod() throws SAXException, IOException, XpathException {
        assertXpathValue("measuringPeriod", "ReadingType/measuringPeriod", xml);
    }

    @Test
    public void marshal_setsTou() throws SAXException, IOException, XpathException {
        assertXpathValue("tou", "ReadingType/tou", xml);
    }

    @Test
    public void marshal_setsAggregate() throws SAXException, IOException, XpathException {
        assertXpathValue("aggregate", "ReadingType/aggregate", xml);
    }

    @Test
    public void marshal_setsArgument() throws SAXException, IOException, XpathException {
        RationalNumber number = new RationalNumber();
        number.setNumerator(new BigInteger("1"));
        number.setDenominator(new BigInteger("3"));

        assertXpathValue("1", "ReadingType/argument/numerator", xml);
        assertXpathValue("3", "ReadingType/argument/denominator", xml);
    }
}

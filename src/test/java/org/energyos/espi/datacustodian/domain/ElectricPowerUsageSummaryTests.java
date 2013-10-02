/*
 * Copyright 2013 EnergyOS.org
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
import static org.energyos.espi.datacustodian.utils.factories.EspiFactory.newElectricPowerUsageSummaryWithUsagePoint;
import static org.junit.Assert.assertEquals;

public class ElectricPowerUsageSummaryTests extends XMLTest {

    private String xml;
    static final String XML_INPUT =
        "<ElectricPowerUsageSummary xmlns=\"http://naesb.org/espi\">" +
            "<billLastPeriod>1</billLastPeriod>" +
            "<billToDate>2</billToDate>" +
            "<costAdditionalLastPeriod>3</costAdditionalLastPeriod>" +
            "<currency>currency</currency>" +
            "<qualityOfReading>qualityOfReading</qualityOfReading>" +
            "<statusTimeStamp>4</statusTimeStamp>" +
            "<ratchetDemandPeriod>" +
                "<duration>1119600</duration>" +
                "<start>1119601</start>" +
            "</ratchetDemandPeriod>" +
            "<billingPeriod>" +
                "<duration>1119602</duration>" +
                "<start>1119603</start>" +
            "</billingPeriod>" +
            "<currentBillingPeriodOverAllConsumption>" +
                "<powerOfTenMultiplier>PowerOfTenMultiplier</powerOfTenMultiplier>" +
                "<timeStamp>1331784000</timeStamp>" +
                "<uom>Uom</uom>" +
                "<value>93018</value>" +
            "</currentBillingPeriodOverAllConsumption>" +
            "<currentDayLastYearNetConsumption>" +
                "<powerOfTenMultiplier>PowerOfTenMultiplier</powerOfTenMultiplier>" +
                "<timeStamp>1331784000</timeStamp>" +
                "<uom>Uom</uom>" +
                "<value>93018</value>" +
            "</currentDayLastYearNetConsumption>" +
            "<currentDayNetConsumption>" +
                "<powerOfTenMultiplier>PowerOfTenMultiplier</powerOfTenMultiplier>" +
                "<timeStamp>1331784000</timeStamp>" +
                "<uom>Uom</uom>" +
                "<value>93018</value>" +
            "</currentDayNetConsumption>" +
            "<currentDayOverallConsumption>" +
                "<powerOfTenMultiplier>PowerOfTenMultiplier</powerOfTenMultiplier>" +
                "<timeStamp>1331784000</timeStamp>" +
                "<uom>Uom</uom>" +
                "<value>93018</value>" +
            "</currentDayOverallConsumption>" +
            "<peakDemand>" +
                "<powerOfTenMultiplier>PowerOfTenMultiplier</powerOfTenMultiplier>" +
                "<timeStamp>1331784000</timeStamp>" +
                "<uom>Uom</uom>" +
                "<value>93018</value>" +
            "</peakDemand>" +
            "<previousDayLastYearOverallConsumption>" +
                "<powerOfTenMultiplier>PowerOfTenMultiplier</powerOfTenMultiplier>" +
                "<timeStamp>1331784000</timeStamp>" +
                "<uom>Uom</uom>" +
                "<value>93018</value>" +
            "</previousDayLastYearOverallConsumption>" +
            "<previousDayNetConsumption>" +
                "<powerOfTenMultiplier>PowerOfTenMultiplier</powerOfTenMultiplier>" +
                "<timeStamp>1331784000</timeStamp>" +
                "<uom>Uom</uom>" +
                "<value>93018</value>" +
            "</previousDayNetConsumption>" +
            "<previousDayOverallConsumption>" +
                "<powerOfTenMultiplier>PowerOfTenMultiplier</powerOfTenMultiplier>" +
                "<timeStamp>1331784000</timeStamp>" +
                "<uom>Uom</uom>" +
                "<value>93018</value>" +
            "</previousDayOverallConsumption>" +
            "<ratchetDemand>" +
                "<powerOfTenMultiplier>PowerOfTenMultiplier</powerOfTenMultiplier>" +
                "<timeStamp>1331784000</timeStamp>" +
                "<uom>Uom</uom>" +
                "<value>93018</value>" +
            "</ratchetDemand>" +
        "</ElectricPowerUsageSummary>";

    private ElectricPowerUsageSummary electricPowerUsageSummary;

    @Before
    public void before() throws JAXBException, FeedException {
        xml = EspiMarshaller.marshal(newElectricPowerUsageSummaryWithUsagePoint());
        electricPowerUsageSummary = EspiMarshaller.<ElectricPowerUsageSummary>unmarshal(XML_INPUT).getValue();
    }

    @Test
    public void unmarshalsElectricPowerUsageSummary() {
        assertEquals(ElectricPowerUsageSummary.class, electricPowerUsageSummary.getClass());
    }

    @Test
    public void unmarshal_setsBillLastPeriod() {
        assertEquals(1L, electricPowerUsageSummary.getBillLastPeriod().longValue());
    }

    @Test
    public void unmarshal_setsBillToDate() {
        assertEquals(2L, electricPowerUsageSummary.getBillToDate().longValue());
    }

    @Test
    public void unmarshal_setsCostAdditionalLastPeriod() {
        assertEquals(3L, electricPowerUsageSummary.getCostAdditionalLastPeriod().longValue());
    }

    @Test
    public void unmarshal_setsCurrency() {
        assertEquals("currency", electricPowerUsageSummary.getCurrency());
    }

    @Test
    public void unmarshal_setsQualityOfReading() {
        assertEquals("qualityOfReading", electricPowerUsageSummary.getQualityOfReading());
    }

    @Test
    public void unmarshal_setsStatusTimeStamp() {
        assertEquals(4L, electricPowerUsageSummary.getStatusTimeStamp());
    }

    @Test
    public void unmarshal_setsRatchetDemandPeriod() {
        assertEquals(1119600L, electricPowerUsageSummary.getRatchetDemandPeriod().getDuration().longValue());
        assertEquals(1119601L, electricPowerUsageSummary.getRatchetDemandPeriod().getStart().longValue());
    }

    @Test
    public void unmarshal_setsBillingPeriod() {
        assertEquals(1119602L, electricPowerUsageSummary.getBillingPeriod().getDuration().longValue());
        assertEquals(1119603L, electricPowerUsageSummary.getBillingPeriod().getStart().longValue());
    }

    @Test
    public void unmarshal_setsCurrentBillingPeriodOverAllConsumption() {
        assertSummaryMeasurement(electricPowerUsageSummary.getCurrentBillingPeriodOverAllConsumption());
    }

    @Test
    public void unmarshal_setsCurrentDayLastYearNetConsumption() {
        assertSummaryMeasurement(electricPowerUsageSummary.getCurrentDayLastYearNetConsumption());
    }

    @Test
    public void unmarshal_setsCurrentDayNetConsumption() {
        assertSummaryMeasurement(electricPowerUsageSummary.getCurrentDayNetConsumption());
    }

    @Test
    public void unmarshal_setsCurrentDayOverallConsumption() {
        assertSummaryMeasurement(electricPowerUsageSummary.getCurrentDayOverallConsumption());
    }

    @Test
    public void unmarshal_setsPeakDemand() {
        assertSummaryMeasurement(electricPowerUsageSummary.getPeakDemand());
    }

    @Test
    public void unmarshal_setsPreviousDayLastYearOverallConsumption() {
        assertSummaryMeasurement(electricPowerUsageSummary.getPreviousDayLastYearOverallConsumption());
    }

    @Test
    public void unmarshal_setsPreviousDayNetConsumption() {
        assertSummaryMeasurement(electricPowerUsageSummary.getPreviousDayNetConsumption());
    }

    @Test
    public void unmarshal_setsPreviousDayOverallConsumption() {
        assertSummaryMeasurement(electricPowerUsageSummary.getPreviousDayOverallConsumption());
    }

    @Test
    public void unmarshal_setsRatchetDemand() {
        assertSummaryMeasurement(electricPowerUsageSummary.getRatchetDemand());
    }

    @Test
    public void marshal_setsBillingPeriod() throws JAXBException, SAXException, IOException, XpathException {
        assertXpathValue("1119600", "/ElectricPowerUsageSummary/billingPeriod/duration", xml);
        assertXpathValue("1119600", "/ElectricPowerUsageSummary/billingPeriod/start", xml);
    }

    @Test
    public void marshal_setsBillLastPeriod() throws JAXBException, SAXException, IOException, XpathException {
        assertXpathValue("15303000", "/ElectricPowerUsageSummary/billLastPeriod", xml);
    }

    @Test
    public void marshal_setsBillToDate() throws JAXBException, SAXException, IOException, XpathException {
        assertXpathValue("1135000", "/ElectricPowerUsageSummary/billToDate", xml);
    }

    @Test
    public void marshal_setsCostAdditionalLastPeriod() throws JAXBException, SAXException, IOException, XpathException {
        assertXpathValue("1346000", "/ElectricPowerUsageSummary/costAdditionalLastPeriod", xml);
    }

    @Test
    public void marshal_setsCurrency() throws JAXBException, SAXException, IOException, XpathException {
        assertXpathValue("840", "/ElectricPowerUsageSummary/currency", xml);
    }

    @Test
    public void marshal_setsCurrentBillingPeriodOverAllConsumption() throws JAXBException, SAXException, IOException, XpathException {
        assertXpathValue("0", "/ElectricPowerUsageSummary/currentBillingPeriodOverAllConsumption/powerOfTenMultiplier", xml);
        assertXpathValue("1331784000", "/ElectricPowerUsageSummary/currentBillingPeriodOverAllConsumption/timeStamp", xml);
        assertXpathValue("72", "/ElectricPowerUsageSummary/currentBillingPeriodOverAllConsumption/uom", xml);
        assertXpathValue("93018", "/ElectricPowerUsageSummary/currentBillingPeriodOverAllConsumption/value", xml);
    }

    @Test
    public void marshal_setsQualityOfReading() throws JAXBException, SAXException, IOException, XpathException {
        assertXpathValue("14", "/ElectricPowerUsageSummary/qualityOfReading", xml);
    }

    @Test
    public void marshal_setsStatusTimeStamp() throws JAXBException, SAXException, IOException, XpathException {
        assertXpathValue("1331784000", "/ElectricPowerUsageSummary/statusTimeStamp", xml);
    }

    @Test
    public void marshal_setsCurrentDayLastYearNetConsumption() throws JAXBException, SAXException, IOException, XpathException {
        assertXpathValue("0", "/ElectricPowerUsageSummary/currentDayLastYearNetConsumption/powerOfTenMultiplier", xml);
        assertXpathValue("1331784000", "/ElectricPowerUsageSummary/currentDayLastYearNetConsumption/timeStamp", xml);
        assertXpathValue("72", "/ElectricPowerUsageSummary/currentDayLastYearNetConsumption/uom", xml);
        assertXpathValue("93018", "/ElectricPowerUsageSummary/currentDayLastYearNetConsumption/value", xml);
    }

    @Test
    public void marshal_setsCurrentDayNetConsumption() throws JAXBException, SAXException, IOException, XpathException {
        assertXpathValue("0", "/ElectricPowerUsageSummary/currentDayNetConsumption/powerOfTenMultiplier", xml);
        assertXpathValue("1331784000", "/ElectricPowerUsageSummary/currentDayNetConsumption/timeStamp", xml);
        assertXpathValue("72", "/ElectricPowerUsageSummary/currentDayNetConsumption/uom", xml);
        assertXpathValue("93018", "/ElectricPowerUsageSummary/currentDayNetConsumption/value", xml);
    }

    @Test
    public void marshal_setsCurrentDayOverallConsumption() throws JAXBException, SAXException, IOException, XpathException {
        assertXpathValue("0", "/ElectricPowerUsageSummary/currentDayOverallConsumption/powerOfTenMultiplier", xml);
        assertXpathValue("1331784000", "/ElectricPowerUsageSummary/currentDayOverallConsumption/timeStamp", xml);
        assertXpathValue("72", "/ElectricPowerUsageSummary/currentDayOverallConsumption/uom", xml);
        assertXpathValue("93018", "/ElectricPowerUsageSummary/currentDayOverallConsumption/value", xml);
    }

    @Test
    public void marshal_setsPeakDemand() throws JAXBException, SAXException, IOException, XpathException {
        assertXpathValue("0", "/ElectricPowerUsageSummary/peakDemand/powerOfTenMultiplier", xml);
        assertXpathValue("1331784000", "/ElectricPowerUsageSummary/peakDemand/timeStamp", xml);
        assertXpathValue("72", "/ElectricPowerUsageSummary/peakDemand/uom", xml);
        assertXpathValue("93018", "/ElectricPowerUsageSummary/peakDemand/value", xml);
    }

    @Test
    public void marshal_setsPreviousDayLastYearOverallConsumption() throws JAXBException, SAXException, IOException, XpathException {
        assertXpathValue("0", "/ElectricPowerUsageSummary/previousDayLastYearOverallConsumption/powerOfTenMultiplier", xml);
        assertXpathValue("1331784000", "/ElectricPowerUsageSummary/previousDayLastYearOverallConsumption/timeStamp", xml);
        assertXpathValue("72", "/ElectricPowerUsageSummary/previousDayLastYearOverallConsumption/uom", xml);
        assertXpathValue("93018", "/ElectricPowerUsageSummary/previousDayLastYearOverallConsumption/value", xml);
    }

    @Test
    public void marshal_setsPreviousDayNetConsumption() throws JAXBException, SAXException, IOException, XpathException {
        assertXpathValue("0", "/ElectricPowerUsageSummary/previousDayNetConsumption/powerOfTenMultiplier", xml);
        assertXpathValue("1331784000", "/ElectricPowerUsageSummary/previousDayNetConsumption/timeStamp", xml);
        assertXpathValue("72", "/ElectricPowerUsageSummary/previousDayNetConsumption/uom", xml);
        assertXpathValue("93018", "/ElectricPowerUsageSummary/previousDayNetConsumption/value", xml);
    }

    @Test
    public void marshal_setsPreviousDayOverallConsumption() throws JAXBException, SAXException, IOException, XpathException {
        assertXpathValue("0", "/ElectricPowerUsageSummary/previousDayOverallConsumption/powerOfTenMultiplier", xml);
        assertXpathValue("1331784000", "/ElectricPowerUsageSummary/previousDayOverallConsumption/timeStamp", xml);
        assertXpathValue("72", "/ElectricPowerUsageSummary/previousDayOverallConsumption/uom", xml);
        assertXpathValue("93018", "/ElectricPowerUsageSummary/previousDayOverallConsumption/value", xml);
    }

    @Test
    public void marshal_setsRatchetDemand() throws JAXBException, SAXException, IOException, XpathException {
        assertXpathValue("0", "/ElectricPowerUsageSummary/ratchetDemand/powerOfTenMultiplier", xml);
        assertXpathValue("1331784000", "/ElectricPowerUsageSummary/ratchetDemand/timeStamp", xml);
        assertXpathValue("72", "/ElectricPowerUsageSummary/ratchetDemand/uom", xml);
        assertXpathValue("93018", "/ElectricPowerUsageSummary/ratchetDemand/value", xml);
    }

    @Test
    public void marshal_setsRatchetDemandPeriod() throws JAXBException, SAXException, IOException, XpathException {
        assertXpathValue("1119600", "/ElectricPowerUsageSummary/ratchetDemandPeriod/duration", xml);
        assertXpathValue("1119600", "/ElectricPowerUsageSummary/ratchetDemandPeriod/start", xml);
    }

    @Test
    public void usagePoint_hasTransientAnnotation() {
        assertAnnotationPresent(ElectricPowerUsageSummary.class, "usagePoint", XmlTransient.class);
    }

    private void assertSummaryMeasurement(SummaryMeasurement sm) {
        assertEquals("PowerOfTenMultiplier", sm.getPowerOfTenMultiplier());
        assertEquals(1331784000L, sm.getTimeStamp().longValue());
        assertEquals("Uom", sm.getUom());
        assertEquals(93018L, sm.getValue().longValue());
    }
}
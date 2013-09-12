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

package org.energyos.espi.datacustodian.domain.xml;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.energyos.espi.datacustodian.atom.XMLTest;
import org.energyos.espi.datacustodian.domain.ElectricPowerUsageSummary;
import org.energyos.espi.datacustodian.utils.factories.Factory;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.StringWriter;

import static org.energyos.espi.datacustodian.Asserts.assertXpathValue;

public class ElectricPowerUsageSummaryTests extends XMLTest {

    private String xml;

    @Before
    public void before() throws JAXBException {
        Marshaller marshaller = JAXBContext.newInstance(ElectricPowerUsageSummary.class).createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(Factory.newUsagePoint().getElectricPowerUsageSummaries().get(0), stringWriter);

        xml = stringWriter.toString();
    }

    @Test
    public void billingPeriod() throws JAXBException, SAXException, IOException, XpathException {
        assertXpathValue("1119600", "/ElectricPowerUsageSummary/billingPeriod/duration", xml);
        assertXpathValue("1119600", "/ElectricPowerUsageSummary/billingPeriod/start", xml);
    }

    @Test
    public void billLastPeriod() throws JAXBException, SAXException, IOException, XpathException {
        assertXpathValue("15303000", "/ElectricPowerUsageSummary/billLastPeriod", xml);
    }

    @Test
    public void billToDate() throws JAXBException, SAXException, IOException, XpathException {
        assertXpathValue("1135000", "/ElectricPowerUsageSummary/billToDate", xml);
    }

    @Test
    public void costAdditionalLastPeriod() throws JAXBException, SAXException, IOException, XpathException {
        assertXpathValue("1346000", "/ElectricPowerUsageSummary/costAdditionalLastPeriod", xml);
    }

    @Test
    public void currency() throws JAXBException, SAXException, IOException, XpathException {
        assertXpathValue("840", "/ElectricPowerUsageSummary/currency", xml);
    }

    @Test
    public void currentBillingPeriodOverAllConsumption() throws JAXBException, SAXException, IOException, XpathException {
        assertXpathValue("0", "/ElectricPowerUsageSummary/currentBillingPeriodOverAllConsumption/powerOfTenMultiplier", xml);
        assertXpathValue("1331784000", "/ElectricPowerUsageSummary/currentBillingPeriodOverAllConsumption/timeStamp", xml);
        assertXpathValue("72", "/ElectricPowerUsageSummary/currentBillingPeriodOverAllConsumption/uom", xml);
        assertXpathValue("93018", "/ElectricPowerUsageSummary/currentBillingPeriodOverAllConsumption/value", xml);
    }

    @Test
    public void qualityOfReading() throws JAXBException, SAXException, IOException, XpathException {
        assertXpathValue("14", "/ElectricPowerUsageSummary/qualityOfReading", xml);
    }

    @Test
    public void statusTimeStamp() throws JAXBException, SAXException, IOException, XpathException {
        assertXpathValue("1331784000", "/ElectricPowerUsageSummary/statusTimeStamp", xml);
    }

    @Test
    public void currentDayLastYearNetConsumption() throws JAXBException, SAXException, IOException, XpathException {
        assertXpathValue("0", "/ElectricPowerUsageSummary/currentDayLastYearNetConsumption/powerOfTenMultiplier", xml);
        assertXpathValue("1331784000", "/ElectricPowerUsageSummary/currentDayLastYearNetConsumption/timeStamp", xml);
        assertXpathValue("72", "/ElectricPowerUsageSummary/currentDayLastYearNetConsumption/uom", xml);
        assertXpathValue("93018", "/ElectricPowerUsageSummary/currentDayLastYearNetConsumption/value", xml);
    }

    @Test
    public void currentDayNetConsumption() throws JAXBException, SAXException, IOException, XpathException {
        assertXpathValue("0", "/ElectricPowerUsageSummary/currentDayNetConsumption/powerOfTenMultiplier", xml);
        assertXpathValue("1331784000", "/ElectricPowerUsageSummary/currentDayNetConsumption/timeStamp", xml);
        assertXpathValue("72", "/ElectricPowerUsageSummary/currentDayNetConsumption/uom", xml);
        assertXpathValue("93018", "/ElectricPowerUsageSummary/currentDayNetConsumption/value", xml);
    }

    @Test
    public void currentDayOverallConsumption() throws JAXBException, SAXException, IOException, XpathException {
        assertXpathValue("0", "/ElectricPowerUsageSummary/currentDayOverallConsumption/powerOfTenMultiplier", xml);
        assertXpathValue("1331784000", "/ElectricPowerUsageSummary/currentDayOverallConsumption/timeStamp", xml);
        assertXpathValue("72", "/ElectricPowerUsageSummary/currentDayOverallConsumption/uom", xml);
        assertXpathValue("93018", "/ElectricPowerUsageSummary/currentDayOverallConsumption/value", xml);
    }

    @Test
    public void peakDemand() throws JAXBException, SAXException, IOException, XpathException {
        assertXpathValue("0", "/ElectricPowerUsageSummary/peakDemand/powerOfTenMultiplier", xml);
        assertXpathValue("1331784000", "/ElectricPowerUsageSummary/peakDemand/timeStamp", xml);
        assertXpathValue("72", "/ElectricPowerUsageSummary/peakDemand/uom", xml);
        assertXpathValue("93018", "/ElectricPowerUsageSummary/peakDemand/value", xml);
    }

    @Test
    public void previousDayLastYearOverallConsumption() throws JAXBException, SAXException, IOException, XpathException {
        assertXpathValue("0", "/ElectricPowerUsageSummary/previousDayLastYearOverallConsumption/powerOfTenMultiplier", xml);
        assertXpathValue("1331784000", "/ElectricPowerUsageSummary/previousDayLastYearOverallConsumption/timeStamp", xml);
        assertXpathValue("72", "/ElectricPowerUsageSummary/previousDayLastYearOverallConsumption/uom", xml);
        assertXpathValue("93018", "/ElectricPowerUsageSummary/previousDayLastYearOverallConsumption/value", xml);
    }

    @Test
    public void previousDayNetConsumption() throws JAXBException, SAXException, IOException, XpathException {
        assertXpathValue("0", "/ElectricPowerUsageSummary/previousDayNetConsumption/powerOfTenMultiplier", xml);
        assertXpathValue("1331784000", "/ElectricPowerUsageSummary/previousDayNetConsumption/timeStamp", xml);
        assertXpathValue("72", "/ElectricPowerUsageSummary/previousDayNetConsumption/uom", xml);
        assertXpathValue("93018", "/ElectricPowerUsageSummary/previousDayNetConsumption/value", xml);
    }

    @Test
    public void previousDayOverallConsumption() throws JAXBException, SAXException, IOException, XpathException {
        assertXpathValue("0", "/ElectricPowerUsageSummary/previousDayOverallConsumption/powerOfTenMultiplier", xml);
        assertXpathValue("1331784000", "/ElectricPowerUsageSummary/previousDayOverallConsumption/timeStamp", xml);
        assertXpathValue("72", "/ElectricPowerUsageSummary/previousDayOverallConsumption/uom", xml);
        assertXpathValue("93018", "/ElectricPowerUsageSummary/previousDayOverallConsumption/value", xml);
    }

    @Test
    public void ratchetDemand() throws JAXBException, SAXException, IOException, XpathException {
        assertXpathValue("0", "/ElectricPowerUsageSummary/ratchetDemand/powerOfTenMultiplier", xml);
        assertXpathValue("1331784000", "/ElectricPowerUsageSummary/ratchetDemand/timeStamp", xml);
        assertXpathValue("72", "/ElectricPowerUsageSummary/ratchetDemand/uom", xml);
        assertXpathValue("93018", "/ElectricPowerUsageSummary/ratchetDemand/value", xml);
    }

    @Test
    public void ratchetDemandPeriod() throws JAXBException, SAXException, IOException, XpathException {
        assertXpathValue("1119600", "/ElectricPowerUsageSummary/ratchetDemandPeriod/duration", xml);
        assertXpathValue("1119600", "/ElectricPowerUsageSummary/ratchetDemandPeriod/start", xml);
    }
}
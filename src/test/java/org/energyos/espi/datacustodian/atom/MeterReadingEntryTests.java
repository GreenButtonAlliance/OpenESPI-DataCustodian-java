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

package org.energyos.espi.datacustodian.atom;


import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Link;
import com.sun.syndication.io.FeedException;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.energyos.espi.datacustodian.domain.*;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.math.BigInteger;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathExists;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MeterReadingEntryTests {

    private MeterReading meterReading;

    @Before
    public void before() {
        XMLUnit.getControlDocumentBuilderFactory().setNamespaceAware(false);

        UsagePoint usagePoint = new UsagePoint();
        usagePoint.setId(1L);
        usagePoint.setDescription("Electric Meter");
        RetailCustomer customer = new RetailCustomer();
        customer.setId(1L);
        usagePoint.setRetailCustomer(customer);
        meterReading = newMeterReading();
        ReadingType readingType = newReadingType();
        meterReading.setReadingType(readingType);
        usagePoint.addMeterReading(meterReading);
    }

    @Test
    public void constructsMeterReadingEntry() throws FeedException, SAXException, IOException, XpathException {


        MeterReadingEntry entry = new MeterReadingEntry(meterReading);
        assertNotNull("entry was null", entry);

        assertEquals("RetailCustomer/1/UsagePoint/1/MeterReading/9", entry.getSelfLink().getHref());
        assertEquals("RetailCustomer/1/UsagePoint/1/MeterReading", entry.getUpLink().getHref());
        assertEquals("ReadingType/8", findReadingTypeLink(entry).getHref());

        Content content = (Content)entry.getContents().get(0);
        assertXpathExists("MeterReading", content.getValue());
    }

    private Link findReadingTypeLink(EspiEntry entry) {
        for (Link link : entry.getRelatedLinks()) {
            if (link.getHref().contains("ReadingType")) {
                return link;
            }
        }
        return null;
    }

    private MeterReading newMeterReading() {
        MeterReading reading = new MeterReading();

        reading.setId(9L);
        reading.setDescription("Electricity consumption");

        return reading;
    }

    private ReadingType newReadingType() {
        ReadingType type = new ReadingType();
        RationalNumber argument = new RationalNumber();
        argument.setNumerator(new BigInteger("1"));
        argument.setDenominator(new BigInteger("3"));
        ReadingInterharmonic interharmonic = new ReadingInterharmonic();
        interharmonic.setNumerator(new BigInteger("1"));
        interharmonic.setDenominator(new BigInteger("6"));

        type.setId(8L);
        type.setDescription("Energy Delivered");
        type.setAccumulationBehaviour("accumulationBehaviour");
        type.setCommodity("commodity");
        type.setDataQualifier("dataQualifier");
        type.setIntervalLength(10L);
        type.setKind("kind");
        type.setPhase("phase");
        type.setPowerOfTenMultiplier("multiplier");
        type.setUom("uom");
        type.setCurrency("currency");
        type.setTou("tou");
        type.setAggregate("aggregate");
        type.setArgument(argument);
        type.setInterharmonic(interharmonic);

        return type;
    }
}
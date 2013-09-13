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
import static org.energyos.espi.datacustodian.utils.factories.EspiFactory.newMeterReading;
import static org.energyos.espi.datacustodian.utils.factories.EspiFactory.newReadingType;
import static org.energyos.espi.datacustodian.utils.factories.EspiFactory.newUsagePoint;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MeterReadingEntryTests extends XMLTest {

    @Test
    public void constructsMeterReadingEntry() throws FeedException, SAXException, IOException, XpathException {
        MeterReadingEntry entry = new MeterReadingEntry(newMeterReading());
        assertNotNull("entry was null", entry);

        assertEquals("RetailCustomer/88/UsagePoint/99/MeterReading/98", entry.getSelfLink().getHref());
        assertEquals("RetailCustomer/88/UsagePoint/99/MeterReading", entry.getUpLink().getHref());
        assertEquals("ReadingType/96", findReadingTypeLink(entry).getHref());

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
}
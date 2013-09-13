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
import com.sun.syndication.io.FeedException;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.IOException;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathExists;
import static org.energyos.espi.datacustodian.utils.factories.EspiFactory.newMeterReading;
import static org.junit.Assert.*;

public class IntervalBlocksEntryTests extends XMLTest {

    private IntervalBlocksEntry entry;

    @Before
    public void before() throws JAXBException, FeedException {
        entry = new IntervalBlocksEntry(newMeterReading().getIntervalBlocks());
    }

    @Test
    public void id() {
        assertEquals("urn:uuid:E8E75691-7F9D-49F3-8BE2-3A74EBF6BFC0", entry.getId());
    }

    @Test
    public void title() {
        assertNull(entry.getTitle());
    }

    @Test
    public void selfLink() {
        assertEquals("RetailCustomer/88/UsagePoint/99/MeterReading/98/IntervalBlock/97", entry.getSelfLink().getHref());
    }

    @Test
    public void upLink() {
        assertEquals("RetailCustomer/88/UsagePoint/99/MeterReading/98/IntervalBlock", entry.getUpLink().getHref());
    }


    @Test
    public void content() throws SAXException, IOException, XpathException {
        String xmlContent = ((Content) entry.getContents().get(0)).getValue();
        xmlContent = "<content>" + xmlContent + "</content>";
        assertXpathExists("content/IntervalBlock[1]", xmlContent);
        assertXpathExists("content/IntervalBlock[2]", xmlContent);
    }

    @Test
    public void published() {
        assertNotNull(entry.getPublished());
    }

    @Test
    public void updated() {
        assertNotNull(entry.getUpdated());
    }
}
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

package org.energyos.espi.datacustodian.utils;


import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.io.FeedException;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.energyos.espi.datacustodian.atom.EspiEntry;
import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.energyos.espi.datacustodian.service.UsagePointService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathExists;
import static org.custommonkey.xmlunit.XMLAssert.assertXpathNotExists;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
@Transactional
public class FeedBuilderMeterReadingTests {

    @Autowired
    UsagePointService usagePointService;

    private Feed feed;
    private EspiEntry entry;
    private List<Content> contents;

    @Before
    public void setup() throws IOException, JAXBException, FeedException {
        XMLUnit.getControlDocumentBuilderFactory().setNamespaceAware(false);
        ClassPathResource sourceFile = new ClassPathResource("/fixtures/15minLP_15Days.xml");
        FeedBuilder builder = new FeedBuilder();
        RetailCustomer customer = new RetailCustomer();
        customer.setId(4L);

        usagePointService.importUsagePoints(customer, sourceFile.getInputStream());
        List<UsagePoint> usagePoints = usagePointService.findAllByRetailCustomer(customer);

        feed =  builder.buildFeed(usagePoints);
        entry = (EspiEntry) feed.getEntries().get(1);
        contents = entry.getContents();
    }

    @Test
    public void returnsEntryWithId() throws FeedException {
        assertNotNull("Entry id was null", entry.getId());
    }

    @Test
    public void returnsEntryWithTitle() throws FeedException {
        assertEquals("Fifteen Minute Electricity Consumption", entry.getTitle());
    }

    @Test
    public void returnsEntryWithSelfLink() throws FeedException {
        assertNotNull("Entry self link was null", entry.getSelfLink());
    }

    @Test
    public void returnsEntryWithUpLink() throws FeedException {
        assertNotNull("Entry up link was null", entry.getUpLink());
    }

    @Test
    public void returnsEntryWithSelfLinkHref() throws FeedException {
        assertNotNull(entry.getSelfLink().getHref());
    }

    @Test
    public void givenUsagePoint_returnsEntryWithUpLinkHref() throws FeedException {
        assertNotNull(entry.getUpLink().getHref());
    }

    @Test
    public void returnsEntryWithUpdatedDate() throws FeedException {
        assertEquals(Date.class, entry.getUpdated().getClass());
    }

    @Test
    public void returnsEntryWithPublishedDate() throws FeedException {
        assertEquals(Date.class, entry.getPublished().getClass());
    }

    @Test
    public void returnsEntryWithContent() throws FeedException {
        assertEquals(1, contents.size());
        assertEquals(Content.class, contents.get(0).getClass());
    }

    @Test
    public void returnsEntryWithContentWithMeterReading() throws FeedException, SAXException, IOException, XpathException {
        assertXpathExists("MeterReading", contents.get(0).getValue());
    }

    @Test
    public void returnsEntryWithContentWithoutUsagePoint() throws FeedException, SAXException, IOException, XpathException {
        assertXpathNotExists("//usagePoint", contents.get(0).getValue());
    }

    @Test
    public void returnsEntryWithContentWithoutReadingType() throws FeedException, SAXException, IOException, XpathException {
        assertXpathNotExists("//readingType", contents.get(0).getValue());
    }

    @Test
    public void returnsEntryWithContentWithoutIntervalBlocks() throws FeedException, SAXException, IOException, XpathException {
        assertXpathNotExists("//intervalBlocks", contents.get(0).getValue());
    }
}

/*
 * Copyright 2013 EnergyOS.org
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package org.energyos.espi.datacustodian.utils;

import org.energyos.espi.datacustodian.models.atom.EntryType;
import org.energyos.espi.datacustodian.models.atom.FeedType;
import org.energyos.espi.datacustodian.models.atom.IdType;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.naesb.espi.UsagePoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
public class ATOMMarshallerTests {

    String FEED_PREFIX = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<?xml-stylesheet type=\"text/xsl\" href=\"GreenButtonDataStyleSheet.xslt\"?>" +
            "<feed xmlns=\"http://www.w3.org/2005/Atom\" " +
            " xsi:schemaLocation=\"http://naesb.org/espi espiDerived.xsd\"" +
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
    String FEED_POSTFIX =  "</feed>";

    @Autowired
    private ATOMMarshaller marshaller;

    @Test(expected = UnmarshallingFailureException.class)
    public void givenInvalidInput_throwsAnException() throws JAXBException {
        marshaller.unmarshal(new ByteArrayInputStream(new byte[0]));
    }

    @Test
    public void givenEmptyFeed_returnsFeed() throws JAXBException {
        String xml = FEED_PREFIX + FEED_POSTFIX;

        assertEquals(FeedType.class, marshaller.unmarshal(new ByteArrayInputStream(xml.getBytes())).getClass());
    }

    @Test
    public void givenFeedWithEntry_returnsFeedWithEntry() throws JAXBException {
        String xml = FEED_PREFIX + "<entry></entry>" + FEED_POSTFIX;
        InputStream xmlStream = new ByteArrayInputStream(xml.getBytes());
        FeedType feed = marshaller.unmarshal(xmlStream);

        assertEquals(EntryType.class, feed.getEntries().get(0).getClass());
    }

    @Test
    public void givenFeedWithUsagePointEntry_returnsFeedWithUsagePoint() throws JAXBException {
        String xml = FEED_PREFIX +
                "  <entry>" +
                "    <content>" +
                "      <UsagePoint xmlns=\"http://naesb.org/espi\"></UsagePoint>" +
                "    </content>" +
                "  </entry>" +
                FEED_POSTFIX;
        InputStream xmlStream = new ByteArrayInputStream(xml.getBytes());
        FeedType feed = marshaller.unmarshal(xmlStream);

        assertEquals(UsagePoint.class, feed.getEntries().get(0).getContent().getUsagePoint().getClass());
    }

    @Test
    public void givenFeedWithId_returnsFeedWithId() throws JAXBException {
        String xml = FEED_PREFIX +
                "   <id>urn:uuid:D7B58EA6-D94D-45D1-A0CA-F8A843AB1080</id>" +
                FEED_POSTFIX;
        InputStream xmlStream = new ByteArrayInputStream(xml.getBytes());
        FeedType feed = marshaller.unmarshal(xmlStream);

        assertEquals(IdType.class, feed.getId().getClass());

    }

    @Test
    public void givenEntryWithId_returnsEntryWithId() throws JAXBException {
        String xml = FEED_PREFIX +
                "   <entry>" +
                "     <id>urn:uuid:D7B58EA6-D94D-45D1-A0CA-F8A843AB1080</id>" +
                "   </entry>" +
                FEED_POSTFIX;
        InputStream xmlStream = new ByteArrayInputStream(xml.getBytes());
        FeedType feed = marshaller.unmarshal(xmlStream);

        assertEquals(IdType.class, feed.getEntries().get(0).getId().getClass());

    }

    @Test
    public void givenEntryWithTitle_returnsEntryWithTitle() throws JAXBException {
        String xml = FEED_PREFIX +
                "   <entry>" +
                "     <title>Entry Title</title>" +
                "   </entry>" +
                FEED_POSTFIX;
        InputStream xmlStream = new ByteArrayInputStream(xml.getBytes());
        FeedType feed = marshaller.unmarshal(xmlStream);

        assertEquals("Entry Title", feed.getEntries().get(0).getTitle());
    }

    @Test
    public void givenEntryWithPublished_returnsEntryWithPublished() throws JAXBException {
        String xml = FEED_PREFIX +
                "   <entry>" +
                "     <published>2000-02-29T00:00:00Z</published>" +
                "   </entry>" +
                FEED_POSTFIX;
        InputStream xmlStream = new ByteArrayInputStream(xml.getBytes());
        FeedType feed = marshaller.unmarshal(xmlStream);

        assertEquals(new DateTime(2000, 2, 29, 0, 0, 0, 0, DateTimeZone.UTC), feed.getEntries().get(0).getPublished());
    }

    @Test
    public void givenEntryWithUpdate_returnsEntryWithUpdated() throws JAXBException {
        String xml = FEED_PREFIX +
                "   <entry>" +
                "     <updated>2000-02-29T00:00:00Z</updated>" +
                "   </entry>" +
                FEED_POSTFIX;
        InputStream xmlStream = new ByteArrayInputStream(xml.getBytes());
        FeedType feed = marshaller.unmarshal(xmlStream);

        assertEquals(new DateTime(2000, 2, 29, 0, 0, 0, 0, DateTimeZone.UTC), feed.getEntries().get(0).getUpdated());
    }
}

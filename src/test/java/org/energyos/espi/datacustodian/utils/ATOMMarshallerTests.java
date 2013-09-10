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

import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.domain.ServiceCategory;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.energyos.espi.datacustodian.models.atom.EntryType;
import org.energyos.espi.datacustodian.models.atom.FeedType;
import org.energyos.espi.datacustodian.models.atom.IdType;
import org.energyos.espi.datacustodian.service.UsagePointService;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.custommonkey.xmlunit.XMLAssert.assertXpathExists;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
@Transactional
public class ATOMMarshallerTests {

    String FEED_PREFIX = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<?xml-stylesheet type=\"text/xsl\" href=\"GreenButtonDataStyleSheet.xslt\"?>" +
            "<feed xmlns=\"http://www.w3.org/2005/Atom\" " +
            " xsi:schemaLocation=\"http://naesb.org/espi espiDerived.xsd\"" +
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
    String FEED_POSTFIX = "</feed>";

    @Autowired
    private ATOMMarshaller marshaller;
    @Autowired
    UsagePointService usagePointService;

    String xmlResult;
    RetailCustomer retailCustomer;
    UsagePoint usagePoint;

    @Before
    public void setup() throws Exception {
        XMLUnit.getControlDocumentBuilderFactory().setNamespaceAware(false);

        ClassPathResource sourceFile = new ClassPathResource("/fixtures/15minLP_15Days.xml");
        FeedBuilder builder = new FeedBuilder();
        retailCustomer = new RetailCustomer();
        retailCustomer.setId(3L);

        usagePointService.importUsagePoints(retailCustomer, sourceFile.getInputStream());
        List<UsagePoint> usagePoints = usagePointService.findAllByRetailCustomer(retailCustomer);
        usagePoint = usagePoints.get(0);

        xmlResult = marshaller.marshal(builder.buildFeed(usagePoints));
    }

    @Test(expected = UnmarshallingFailureException.class)
    public void unmarshal_givenInvalidInput_throwsAnException() throws JAXBException {
        marshaller.unmarshal(new ByteArrayInputStream(new byte[0]));
    }

    @Test
    public void unmarshal_givenEmptyFeed_returnsFeed() throws JAXBException {
        String xml = FEED_PREFIX + FEED_POSTFIX;

        assertEquals(FeedType.class, marshaller.unmarshal(new ByteArrayInputStream(xml.getBytes())).getClass());
    }

    @Test
    public void unmarshal_givenFeedWithEntry_returnsFeedWithEntry() throws JAXBException {
        String xml = FEED_PREFIX + "<entry></entry>" + FEED_POSTFIX;
        InputStream xmlStream = new ByteArrayInputStream(xml.getBytes());
        FeedType feed = marshaller.unmarshal(xmlStream);

        assertEquals(EntryType.class, feed.getEntries().get(0).getClass());
    }

    @Test
    public void unmarshal_givenFeedWithUsagePointEntry_returnsFeedWithUsagePoint() throws JAXBException {
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
    public void unmarshal_givenFeedWithId_returnsFeedWithId() throws JAXBException {
        String xml = FEED_PREFIX +
                "   <id>urn:uuid:D7B58EA6-D94D-45D1-A0CA-F8A843AB1080</id>" +
                FEED_POSTFIX;
        InputStream xmlStream = new ByteArrayInputStream(xml.getBytes());
        FeedType feed = marshaller.unmarshal(xmlStream);

        assertEquals(IdType.class, feed.getId().getClass());
    }

    @Test
    public void unmarshal_givenEntryWithId_returnsEntryWithId() throws JAXBException {
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
    public void unmarshal_givenEntryWithTitle_returnsEntryWithTitle() throws JAXBException {
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
    public void unmarshal_givenEntryWithPublished_returnsEntryWithPublished() throws JAXBException {
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
    public void unmarshal_givenEntryWithUpdate_returnsEntryWithUpdated() throws JAXBException {
        String xml = FEED_PREFIX +
                "   <entry>" +
                "     <updated>2000-02-29T00:00:00Z</updated>" +
                "   </entry>" +
                FEED_POSTFIX;
        InputStream xmlStream = new ByteArrayInputStream(xml.getBytes());
        FeedType feed = marshaller.unmarshal(xmlStream);

        assertEquals(new DateTime(2000, 2, 29, 0, 0, 0, 0, DateTimeZone.UTC), feed.getEntries().get(0).getUpdated());
    }

    @Test
    public void unmarshal_givenXML_returnsFeed() throws JAXBException, FileNotFoundException {
        String xml = FEED_PREFIX +
                "    <entry>\n" +
                "        <id>urn:uuid:7BC41774-7190-4864-841C-861AC76D46C2</id>\n" +
                "        <link rel=\"self\" href=\"RetailCustomer/9b6c7063/UsagePoint/01\"/>\n" +
                "        <link rel=\"up\" href=\"RetailCustomer/9b6c7063/UsagePoint\"/>\n" +
                "        <link rel=\"related\" href=\"RetailCustomer/9b6c7063/UsagePoint/01/MeterReading\"/>\n" +
                "        <link rel=\"related\" href=\"RetailCustomer/9b6c7063/UsagePoint/01/ElectricPowerUsageSummary\"/>\n" +
                "        <link rel=\"related\" href=\"LocalTimeParameters/01\"/>\n" +
                "        <title>Electric meter</title>\n" +
                "        <content>\n" +
                "            <UsagePoint xmlns=\"http://naesb.org/espi\">\n" +
                "                <ServiceCategory>\n" +
                "                    <kind>3</kind>\n" +
                "                </ServiceCategory>\n" +
                "            </UsagePoint>\n" +
                "        </content>\n" +
                "        <published>2012-10-24T00:00:00Z</published>\n" +
                "        <updated>2012-10-24T00:00:00Z</updated>\n" +
                "    </entry>\n" +
                FEED_POSTFIX;

        InputStream xmlStream = new ByteArrayInputStream(xml.getBytes());
        FeedType feed = marshaller.unmarshal(xmlStream);

        assertEquals(FeedType.class, feed.getClass());
    }

    @Test
    public void importsServiceCategory() throws JAXBException {
        String xml = FEED_PREFIX +
                "    <entry>\n" +
                "        <id>urn:uuid:7BC41774-7190-4864-841C-861AC76D46C2</id>\n" +
                "        <link rel=\"self\" href=\"RetailCustomer/9b6c7063/UsagePoint/01\"/>\n" +
                "        <link rel=\"up\" href=\"RetailCustomer/9b6c7063/UsagePoint\"/>\n" +
                "        <link rel=\"related\" href=\"RetailCustomer/9b6c7063/UsagePoint/01/MeterReading\"/>\n" +
                "        <link rel=\"related\" href=\"RetailCustomer/9b6c7063/UsagePoint/01/ElectricPowerUsageSummary\"/>\n" +
                "        <link rel=\"related\" href=\"LocalTimeParameters/01\"/>\n" +
                "        <title>Electric meter</title>\n" +
                "        <content>\n" +
                "            <UsagePoint xmlns=\"http://naesb.org/espi\">\n" +
                "                <ServiceCategory>\n" +
                "                    <kind>3</kind>\n" +
                "                </ServiceCategory>\n" +
                "            </UsagePoint>\n" +
                "        </content>\n" +
                "        <published>2012-10-24T00:00:00Z</published>\n" +
                "        <updated>2012-10-24T00:00:00Z</updated>\n" +
                "    </entry>\n" +
                FEED_POSTFIX;

        InputStream xmlStream = new ByteArrayInputStream(xml.getBytes());
        FeedType feed = marshaller.unmarshal(xmlStream);
        assertEquals(ServiceCategory.class, feed.getEntries().get(0).getContent().getUsagePoint().getServiceCategory().getClass());
    }

    @Test
    public void marshal_returnsAtomFeed_withRequiredProperties() throws Exception {
        assertXpathExists("feed", xmlResult);
        assertXpathExists("feed/title", xmlResult);
        assertXpathExists("feed/id", xmlResult);
    }

    @Test
    public void marshal_returnsEntryWithId() throws SAXException, IOException, XpathException {
        assertXpathEvaluatesTo(usagePoint.getId().toString(), "/feed/entry/id", xmlResult);
    }

    @Test
    public void marshal_returnsEntryWithSelfLink() throws SAXException, IOException, XpathException {
        assertXpathEvaluatesTo("RetailCustomer/" + retailCustomer.getId() + "/UsagePoint/" + usagePoint.getId(),
                "feed/entry[1]/link[@rel='self']/@href", xmlResult);
    }

    @Test
    public void marshal_returnsEntryWithUpLink() throws SAXException, IOException, XpathException {
        assertXpathEvaluatesTo("RetailCustomer/" + retailCustomer.getId() + "/UsagePoint",
                "feed/entry[1]/link[@rel='up']/@href", xmlResult);
    }

    @Test
    public void marshal_returnsEntryWithMeterReadingRelatedLink() throws SAXException, IOException, XpathException {
        assertXpathEvaluatesTo("RetailCustomer/" + retailCustomer.getId() + "/UsagePoint/" + usagePoint.getId() + "/MeterReading",
                "feed/entry[1]/link[@rel='related']/@href", xmlResult);
    }

    @Test
    public void marshal_returnsEntryWithTitle() throws SAXException, IOException, XpathException {
        assertXpathEvaluatesTo("Front Electric Meter", "/feed/entry/title", xmlResult);
    }

    @Test
    public void marshal_returnsEntryWithContent() throws SAXException, IOException, XpathException {
        assertXpathExists("/feed/entry[1]/content", xmlResult);
    }

    @Test
    public void marshal_returnsEntryWithPublishedDate() throws SAXException, IOException, XpathException {
        assertXpathExists("/feed/entry[1]/published", xmlResult);
    }

    @Test
    public void marshal_returnsEntryWithUpdatedDate() throws SAXException, IOException, XpathException {
        assertXpathExists("/feed/entry[1]/updated", xmlResult);
    }

    @Test
    public void marshal_returnsEntryWithUsagePointContent() throws SAXException, IOException, XpathException {
        assertXpathExists("/feed/entry[1]/content/UsagePoint", xmlResult);
    }

    @Test
    public void marshal_returnsEntryWithServiceCategoryContent() throws SAXException, IOException, XpathException {
        assertXpathExists("/feed/entry[1]/content/UsagePoint/ServiceCategory/kind", xmlResult);
    }
}

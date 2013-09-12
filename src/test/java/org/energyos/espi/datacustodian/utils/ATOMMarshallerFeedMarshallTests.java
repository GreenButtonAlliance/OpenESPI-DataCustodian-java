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

import com.sun.syndication.io.FeedException;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.energyos.espi.datacustodian.atom.XMLTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import java.io.IOException;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathExists;
import static org.energyos.espi.datacustodian.Asserts.assertXpathValue;
import static org.energyos.espi.datacustodian.utils.factories.FeedFactory.newFeed;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
@Transactional
public class ATOMMarshallerFeedMarshallTests extends XMLTest {

    @Autowired
    private ATOMMarshaller marshaller;

    private String newFeedXML() throws FeedException {
        return marshaller.marshal(newFeed());
    }

    @Test
    public void feedId() throws FeedException, SAXException, IOException, XpathException {
        assertXpathValue("urn:uuid:0071C5A7-91CF-434E-8BCE-C38AC8AF215D", "/feed/id", newFeedXML());
    }

    @Test
    public void feedTitle() throws FeedException, SAXException, IOException, XpathException {
        assertXpathValue("Feed title", "/feed/title", newFeedXML());
    }

    @Test
    public void feedUpdated() throws FeedException, SAXException, IOException, XpathException {
        assertXpathValue("2013-12-28T05:00:00Z", "/feed/updated", newFeedXML());
    }

    @Test
    public void feedSelfLink() throws FeedException, SAXException, IOException, XpathException {
        assertXpathValue("/ThirdParty/83e269c1/Batch", "/feed/link[@rel='self']/@href", newFeedXML());
    }

    @Test
    public void feedEntries() throws FeedException, SAXException, IOException, XpathException {
        assertXpathExists("/feed/entry[1]", newFeedXML());
        assertXpathExists("/feed/entry[2]", newFeedXML());
    }

    @Test
    public void entryId() throws FeedException, SAXException, IOException, XpathException {
        assertXpathValue("urn:uuid:7BC41774-7190-4864-841C-861AC76D46C2", "/feed/entry[1]/id", newFeedXML());
    }

    @Test
    public void entrySelfLink() throws FeedException, SAXException, IOException, XpathException {
        assertXpathValue("RetailCustomer/9b6c7063/UsagePoint/01", "/feed/entry[1]/link[@rel='self']/@href", newFeedXML());
    }

    @Test
    public void entryUpLink() throws FeedException, SAXException, IOException, XpathException {
        assertXpathValue("RetailCustomer/9b6c7063/UsagePoint", "/feed/entry[1]/link[@rel='up']/@href", newFeedXML());
    }

    @Test
    public void entryRelatedLink() throws FeedException, SAXException, IOException, XpathException {
        assertXpathValue("RetailCustomer/9b6c7063/UsagePoint/01/MeterReading", "/feed/entry[1]/link[@rel='related']/@href", newFeedXML());
    }

    @Test
    public void entryTitle() throws FeedException, SAXException, IOException, XpathException {
        assertXpathValue("Front Electric Meter", "/feed/entry[1]/title", newFeedXML());
    }

    @Test
    public void entryContent() throws FeedException, SAXException, IOException, XpathException {
        assertXpathExists("/feed/entry[1]/content/UsagePoint", newFeedXML());
    }

    @Test
    public void entryPublished() throws FeedException, SAXException, IOException, XpathException {
        assertXpathValue("2012-11-15T05:00:00Z", "/feed/entry[1]/published", newFeedXML());
    }

    @Test
    public void entryUpdated() throws FeedException, SAXException, IOException, XpathException {
        assertXpathValue("2012-11-17T05:00:00Z", "/feed/entry[1]/updated", newFeedXML());
    }
}

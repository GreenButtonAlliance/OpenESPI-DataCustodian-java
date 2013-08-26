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

    @Autowired
    private ATOMMarshaller marshaller;

    @Test(expected = UnmarshallingFailureException.class)
    public void givenInvalidInput_throwsAnException() throws JAXBException {
        marshaller.unmarshal(new ByteArrayInputStream(new byte[0]));
    }

    @Test
    public void givenEmptyFeed_returnsFeed() throws JAXBException {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                     "<?xml-stylesheet type=\"text/xsl\" href=\"GreenButtonDataStyleSheet.xslt\"?>" +
                     "<feed xmlns=\"http://www.w3.org/2005/Atom\" " +
                     " xsi:schemaLocation=\"http://naesb.org/espi espiDerived.xsd\"" +
                     " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"></feed>";

        assertEquals(FeedType.class, marshaller.unmarshal(new ByteArrayInputStream(xml.getBytes())).getClass());
    }

    @Test
    public void givenFeedWithEntry_returnsFeedWithEntry() throws JAXBException {
        String xml ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<?xml-stylesheet type=\"text/xsl\" href=\"GreenButtonDataStyleSheet.xslt\"?>" +
                "<feed xmlns=\"http://www.w3.org/2005/Atom\" " +
                " xsi:schemaLocation=\"http://naesb.org/espi espiDerived.xsd\"" +
                " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                "  <entry></entry>" +
                "</feed>";
        InputStream xmlStream = new ByteArrayInputStream(xml.getBytes());
        FeedType feed = marshaller.unmarshal(xmlStream);

        assertEquals(EntryType.class, feed.getEntries().get(0).getClass());
    }

    @Test
    public void givenFeedWithUsagePointEntry_returnsFeedWithUsagePoint() throws JAXBException {
        String xml ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<?xml-stylesheet type=\"text/xsl\" href=\"GreenButtonDataStyleSheet.xslt\"?>" +
                "<feed xmlns=\"http://www.w3.org/2005/Atom\" " +
                " xsi:schemaLocation=\"http://naesb.org/espi espiDerived.xsd\"" +
                " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                "  <entry>" +
                "    <content>" +
                "      <UsagePoint xmlns=\"http://naesb.org/espi\"></UsagePoint>" +
                "    </content>" +
                "  </entry>" +
                "</feed>";
        InputStream xmlStream = new ByteArrayInputStream(xml.getBytes());
        FeedType feed = marshaller.unmarshal(xmlStream);

        assertEquals(UsagePoint.class, feed.getEntries().get(0).getContent().getUsagePoint().getClass());
    }
}

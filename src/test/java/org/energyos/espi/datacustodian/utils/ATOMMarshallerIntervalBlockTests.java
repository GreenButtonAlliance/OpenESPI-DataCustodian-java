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

import org.energyos.espi.datacustodian.domain.IntervalBlock;
import org.energyos.espi.datacustodian.models.atom.FeedType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
public class ATOMMarshallerIntervalBlockTests {

    String FEED_PREFIX = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<?xml-stylesheet type=\"text/xsl\" href=\"GreenButtonDataStyleSheet.xslt\"?>" +
            "<feed xmlns=\"http://www.w3.org/2005/Atom\" " +
            " xsi:schemaLocation=\"http://naesb.org/espi espiDerived.xsd\"" +
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
    String FEED_POSTFIX = "</feed>";

    @Autowired
    private ATOMMarshaller marshaller;

    @Test
    public void unmarshal_givenMultipleIntervalBlocks_returnsIntervalBlocks() throws JAXBException {
        String xml = FEED_PREFIX +
                "  <entry>" +
                "    <content>" +
                "      <IntervalBlock xmlns=\"http://naesb.org/espi\"/>\n" +
                "      <IntervalBlock xmlns=\"http://naesb.org/espi\"/>\n" +
                "    </content>" +
                "  </entry>" +
                FEED_POSTFIX;
        InputStream xmlStream = new ByteArrayInputStream(xml.getBytes());
        FeedType feed = marshaller.unmarshal(xmlStream);

        assertEquals(IntervalBlock.class, feed.getEntries().get(0).getContent().getIntervalBlocks().get(0).getClass());
        assertEquals(IntervalBlock.class, feed.getEntries().get(0).getContent().getIntervalBlocks().get(1).getClass());
    }

    @Test
    public void unmarshal_givenFixture_returnsIntervalBlocks() throws IOException, JAXBException {
        ClassPathResource sourceFile = new ClassPathResource("/fixtures/15minLP_15Days.xml");
        FeedType feedType = marshaller.unmarshal(sourceFile.getInputStream());
        assertEquals(IntervalBlock.class, feedType.getEntries().get(3).getContent().getIntervalBlocks().get(0).getClass());
        assertEquals(IntervalBlock.class, feedType.getEntries().get(3).getContent().getIntervalBlocks().get(1).getClass());
    }
}

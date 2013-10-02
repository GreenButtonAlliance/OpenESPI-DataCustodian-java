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

package org.energyos.espi.datacustodian.domain;

import com.sun.syndication.io.FeedException;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.energyos.espi.datacustodian.atom.XMLTest;
import org.energyos.espi.datacustodian.models.atom.adapters.ReadingQualityAdapter;
import org.energyos.espi.datacustodian.utils.EspiMarshaller;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.IOException;

import static org.energyos.espi.datacustodian.support.Asserts.assertXpathValue;
import static org.energyos.espi.datacustodian.support.TestUtils.assertAnnotationPresent;
import static org.energyos.espi.datacustodian.utils.factories.EspiFactory.newIntervalReading;
import static org.junit.Assert.assertEquals;

public class ReadingQualityTests extends XMLTest {

    static final String XML_INPUT =
            "<ReadingQuality xmlns=\"http://naesb.org/espi\">" +
                "<quality>quality1</quality>" +
            "</ReadingQuality>";
    private ReadingQuality readingQuality;

    @Before
    public void before() throws Exception {
        ReadingQualityAdapter intervalBlockAdapter = new ReadingQualityAdapter();
        JAXBElement<ReadingQuality> readingQualityJAXBElement = EspiMarshaller.unmarshal(XML_INPUT);
        readingQuality = intervalBlockAdapter.unmarshal(readingQualityJAXBElement);
    }

    @Test
    public void unmarshalsReadingQuality() {
        assertEquals(ReadingQuality.class, readingQuality.getClass());
    }

    @Test
    public void unmarshal_setsQuality() {
        assertEquals("quality1", readingQuality.getQuality());
    }

    @Test
    public void marshal_setsQuality() throws FeedException, SAXException, IOException, XpathException {
        assertXpathValue("100", "IntervalReading/cost", EspiMarshaller.marshal(newIntervalReading()));
    }

    @Test
    public void intervalReading_hasTransientAnnotation() {
        assertAnnotationPresent(ReadingQuality.class, "intervalReading", XmlTransient.class);
    }
}
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

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.energyos.espi.datacustodian.atom.XMLTest;
import org.energyos.espi.datacustodian.models.atom.adapters.IntervalReadingAdapter;
import org.energyos.espi.datacustodian.utils.EspiMarshaller;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.IOException;

import static org.energyos.espi.datacustodian.Asserts.assertXpathValue;
import static org.energyos.espi.datacustodian.support.TestUtils.assertAnnotationPresent;
import static org.energyos.espi.datacustodian.utils.factories.EspiFactory.newIntervalReading;
import static org.junit.Assert.assertEquals;

public class IntervalReadingTests extends XMLTest {

    static final String XML_INPUT =
        "<IntervalReading xmlns=\"http://naesb.org/espi\">" +
            "<cost>100</cost>" +
            "<ReadingQuality>" +
                "<quality>quality1</quality>" +
            "</ReadingQuality>" +
            "<ReadingQuality>" +
                "<quality>quality2</quality>" +
            "</ReadingQuality>" +
            "<timePeriod>" +
                "<duration>3</duration>" +
                "<start>4</start>" +
            "</timePeriod>" +
            "<value>6</value>" +
        "</IntervalReading>";

    private IntervalReading intervalReading;
    private String xml;

    @Before
    public void before() throws Exception {
        xml = EspiMarshaller.marshal(newIntervalReading());

        IntervalReadingAdapter intervalReadingAdapter = new IntervalReadingAdapter();
        JAXBElement<IntervalReading> intervalReadingJAXBElement = EspiMarshaller.unmarshal(XML_INPUT);
        intervalReading = intervalReadingAdapter.unmarshal(intervalReadingJAXBElement);
    }

    @Test
    public void unmarshalsIntervalReading() {
        assertEquals(IntervalReading.class, intervalReading.getClass());
    }

    @Test
    public void unmarshal_setsCost() {
        assertEquals(100L, intervalReading.getCost().longValue());
    }

    @Test
    public void unmarshal_setsReadingQualities() {
        assertEquals("quality1", intervalReading.getReadingQualities().get(0).getQuality());
        assertEquals(intervalReading, intervalReading.getReadingQualities().get(0).getIntervalReading());
        assertEquals("quality2", intervalReading.getReadingQualities().get(1).getQuality());
        assertEquals(intervalReading, intervalReading.getReadingQualities().get(1).getIntervalReading());
    }

    @Test
    public void unmarshal_setsTimePeriod() {
        assertEquals(3L, intervalReading.getTimePeriod().getDuration().longValue());
        assertEquals(4L, intervalReading.getTimePeriod().getStart().longValue());
    }

    @Test
    public void unmarshal_setsValue() {
        assertEquals(6L, intervalReading.getValue().longValue());
    }

    @Test
    public void intervalBlock_hasTransientAnnotation() {
        assertAnnotationPresent(IntervalReading.class, "intervalBlock", XmlTransient.class);
    }

    @Test
    public void marshal_setsCost() throws SAXException, IOException, XpathException {
        assertXpathValue("100", "IntervalReading/cost", xml);
    }

    @Test
    public void marshal_setsReadingQualities() throws SAXException, IOException, XpathException {
        assertXpathValue("quality1", "IntervalReading/ReadingQuality[1]/quality", xml);
        assertXpathValue("quality2", "IntervalReading/ReadingQuality[2]/quality", xml);
    }

    @Test
    public void marshal_setsTimePeriod() throws SAXException, IOException, XpathException {
        assertXpathValue("86401", "IntervalReading/timePeriod/duration", xml);
        assertXpathValue("1330578001", "IntervalReading/timePeriod/start", xml);
    }

    @Test
    public void marshal_setsValue() throws SAXException, IOException, XpathException {
        assertXpathValue("6", "IntervalReading/value", xml);
    }
}
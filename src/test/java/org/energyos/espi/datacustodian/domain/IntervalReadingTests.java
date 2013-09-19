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

import org.energyos.espi.datacustodian.atom.XMLTest;
import org.energyos.espi.datacustodian.models.atom.adapters.IntervalReadingAdapter;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlTransient;
import java.io.ByteArrayInputStream;

import static org.energyos.espi.datacustodian.support.TestUtils.assertAnnotationPresent;
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

    @Before
    public void before() throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance("org.energyos.espi.datacustodian.models.atom");
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        JAXBElement<IntervalReading> jaxbElement = (JAXBElement<IntervalReading>) unmarshaller.unmarshal(new ByteArrayInputStream(XML_INPUT.getBytes()));
        IntervalReadingAdapter intervalReadingAdapter = new IntervalReadingAdapter();
        intervalReading = intervalReadingAdapter.unmarshal(jaxbElement);
    }

    @Test
    public void unmarshalsIntervalReading() {
        assertEquals(IntervalReading.class, intervalReading.getClass());
    }

    @Test
    public void unmarshal_setsCost() {
        assertEquals(100L, intervalReading.getCost().longValue());
    };

    @Test
    public void unmarshal_setsReadingQualities() {
        assertEquals("quality1", intervalReading.getReadingQualities().get(0).getQuality());
        assertEquals(intervalReading, intervalReading.getReadingQualities().get(0).getIntervalReading());
        assertEquals("quality2", intervalReading.getReadingQualities().get(1).getQuality());
        assertEquals(intervalReading, intervalReading.getReadingQualities().get(1).getIntervalReading());
    };

    @Test
    public void unmarshal_setsTimePeriod() {
        assertEquals(3L, intervalReading.getTimePeriod().getDuration().longValue());
        assertEquals(4L, intervalReading.getTimePeriod().getStart().longValue());
    };

    @Test
    public void unmarshal_setsValue() {
        assertEquals(6L, intervalReading.getValue().longValue());
    };

    @Test
    public void intervalBlock_hasTransientAnnotation() {
        assertAnnotationPresent(IntervalReading.class, "intervalBlock", XmlTransient.class);
    }
}
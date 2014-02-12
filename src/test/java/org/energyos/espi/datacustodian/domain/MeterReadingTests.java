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

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static org.custommonkey.xmlunit.XMLAssert.assertXpathExists;
import static org.energyos.espi.datacustodian.support.TestUtils.assertAnnotationPresent;
import static org.energyos.espi.datacustodian.utils.factories.EspiFactory.newMeterReadingWithUsagePoint;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlTransient;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.energyos.espi.common.domain.MeterReading;
import org.energyos.espi.common.domain.UsagePoint;
import org.energyos.espi.common.utils.EspiMarshaller;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.sun.syndication.io.FeedException;

public class MeterReadingTests extends XMLTest {

    @Test
    public void unmarshalsMeterReading() throws SAXException, IOException, XpathException, FeedException, JAXBException {
        final String XML_INPUT = "<MeterReading xmlns=\"http://naesb.org/espi\"/>";

        assertEquals(MeterReading.class, EspiMarshaller.unmarshal(XML_INPUT).getValue().getClass());
    }

    @Test
    public void marshalsMeterReading() throws SAXException, IOException, XpathException, FeedException {
        assertXpathExists("espi:MeterReading", EspiMarshaller.marshal(newMeterReadingWithUsagePoint()));
    }

    @Test
    public void usagePoint_hasTransientAnnotation() {
        assertAnnotationPresent(MeterReading.class, "usagePoint", XmlTransient.class);
    }

    @Test
    public void intervalBlocks_hasTransientAnnotation() {
        assertAnnotationPresent(MeterReading.class, "intervalBlocks", XmlTransient.class);
    }

    @Test
    public void readingType_hasTransientAnnotation() {
        assertAnnotationPresent(MeterReading.class, "readingType", XmlTransient.class);
    }

    @Test
    public void setUpResource() {
        MeterReading meterReading = new MeterReading();
        UsagePoint usagePoint = new UsagePoint();
        meterReading.setUpResource(usagePoint);

        assertThat(meterReading.getUsagePoint(), equalTo(usagePoint));
    }
}

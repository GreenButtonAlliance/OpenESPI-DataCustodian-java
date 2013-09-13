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
import org.energyos.espi.datacustodian.utils.EspiMarshaller;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.validation.constraints.NotNull;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlTransient;
import java.io.IOException;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathExists;
import static org.energyos.espi.datacustodian.Asserts.assertXpathValue;
import static org.energyos.espi.datacustodian.support.TestUtils.assertAnnotationPresent;
import static org.energyos.espi.datacustodian.utils.factories.EspiFactory.newUsagePoint;

public class UsagePointTests extends XMLTest {

    private String xml;

    @Before
    public void before() throws JAXBException, FeedException {
        xml = EspiMarshaller.marshal(newUsagePoint());
    }

    @Test
    public void usagePoint() throws SAXException, IOException, XpathException {
        assertXpathExists("UsagePoint", xml);
    }

    @Test
    public void roleFlags() throws SAXException, IOException, XpathException {
        assertXpathValue("726F6C6520666C616773", "UsagePoint/roleFlags", xml);
    }

    @Test
    public void serviceCategory() throws SAXException, IOException, XpathException {
        assertXpathValue("0", "UsagePoint/ServiceCategory/kind", xml);
    }

    @Test
    public void status() throws SAXException, IOException, XpathException {
        assertXpathValue("5", "UsagePoint/status", xml);
    }

    @Test
    public void validations() {
        assertAnnotationPresent(UsagePoint.class, "serviceCategory", NotNull.class);
    }

    @Test
    public void meterReadings_hasTransientAnnotation() {
        assertAnnotationPresent(UsagePoint.class, "meterReadings", XmlTransient.class);
    }

    @Test
    public void retailCustomer_hasTransientAnnotation() {
        assertAnnotationPresent(UsagePoint.class, "retailCustomer", XmlTransient.class);
    }

    @Test
    public void electricPowerUsageSummaries_hasTransientAnnotation() {
        assertAnnotationPresent(UsagePoint.class, "electricPowerUsageSummaries", XmlTransient.class);
    }
}

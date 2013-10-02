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

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlTransient;
import java.io.IOException;
import java.util.Set;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathExists;
import static org.energyos.espi.datacustodian.support.Asserts.assertXpathValue;
import static org.energyos.espi.datacustodian.support.TestUtils.assertAnnotationPresent;
import static org.energyos.espi.datacustodian.utils.factories.EspiFactory.newUsagePoint;
import static org.junit.Assert.*;

public class UsagePointTests extends XMLTest {

    static final String XML_INPUT =
            "<UsagePoint xmlns=\"http://naesb.org/espi\">" +
                "<ServiceCategory>" +
                    "<kind>0</kind>" +
                "</ServiceCategory>" +
            "</UsagePoint>";

    private UsagePoint usagePoint;
    private String xml;

    @Before
    public void before() throws JAXBException, FeedException {
        xml = EspiMarshaller.marshal(newUsagePoint());
        usagePoint = EspiMarshaller.<UsagePoint>unmarshal(XML_INPUT).getValue();
    }

    @Test
    public void unmarshalsUsagePoint() {
        assertEquals(UsagePoint.class, usagePoint.getClass());
    }

    @Test
    public void unmarshalsServiceCategory() {
        assertEquals(ServiceCategory.class, usagePoint.getServiceCategory().getClass());
    }

    @Test
    public void unmarshalsServiceKind() {
        assertEquals(new Long(0L), usagePoint.getServiceCategory().getKind());
    }

    @Test
    public void marshalUsagePoint() throws SAXException, IOException, XpathException {
        assertXpathExists("UsagePoint", xml);
    }

    @Test
    public void marshal_setsRoleFlags() throws SAXException, IOException, XpathException {
        assertXpathValue("726F6C6520666C616773", "UsagePoint/roleFlags", xml);
    }

    @Test
    public void marshal_setsServiceCategory() throws SAXException, IOException, XpathException {
        assertXpathValue("0", "UsagePoint/ServiceCategory/kind", xml);
    }

    @Test
    public void marshal_setsStatus() throws SAXException, IOException, XpathException {
        assertXpathValue("5", "UsagePoint/status", xml);
    }

    @Test
    public void isValid() throws Exception {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        UsagePoint usagePoint = new UsagePoint();
        usagePoint.setMRID("urn:uuid:E8E75691-7F9D-49F3-8BE2-3A74EBF6BFC0");
        usagePoint.setServiceCategory(new ServiceCategory(ServiceCategory.ELECTRICITY_SERVICE));

        Set<ConstraintViolation<UsagePoint>> violations = validator.validate(usagePoint);

        assertTrue(violations.isEmpty());
    }

    @Test
    public void isInvalid() throws Exception {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        UsagePoint usagePoint = new UsagePoint();

        Set<ConstraintViolation<UsagePoint>> violations = validator.validate(usagePoint);

        assertFalse(violations.isEmpty());
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

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

import cucumber.api.java.Before;
import org.custommonkey.xmlunit.XMLUnit;
import org.energyos.espi.datacustodian.domain.ServiceCategory;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathExists;
import static org.energyos.espi.datacustodian.Asserts.assertXpathValue;
import static org.junit.Assert.assertEquals;

public class EspiMarshallerTests {

    @BeforeClass
    public static void before() {
        XMLUnit.getControlDocumentBuilderFactory().setNamespaceAware(false);
    }

    @Test
    public void marshal_with_marshallableObject_returnsValidXml() throws Exception {
        assertXpathExists("UsagePoint", EspiMarshaller.marshal(newUsagePoint()));
    }

    @Test
    public void marshal_with_marshallableObject_returnsXmlWithServiceCategory() throws Exception {
        assertXpathValue("1", "UsagePoint/ServiceCategory/kind", EspiMarshaller.marshal(newUsagePoint()));
    }

    private UsagePoint newUsagePoint() {
        UsagePoint usagePoint = new UsagePoint();

        ServiceCategory serviceCategory = new ServiceCategory();
        serviceCategory.setKind(1L);

        usagePoint.setServiceCategory(serviceCategory);

        return usagePoint;
    }
}

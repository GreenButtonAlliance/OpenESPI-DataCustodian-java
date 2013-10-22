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
import org.energyos.espi.datacustodian.atom.XMLTest;
import org.energyos.espi.datacustodian.utils.EspiMarshaller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.xml.bind.JAXBException;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/test-context.xml")
public class UsagePointUnmarshallerTests extends XMLTest {
    static final String XML_INPUT = "" +
            "<UsagePoint xmlns=\"http://naesb.org/espi\">" +
            "   <ServiceCategory>" +
            "       <kind>0</kind>" +
            "   </ServiceCategory>" +
            "</UsagePoint>";

    private UsagePoint usagePoint;

    @Before
    public void before() throws JAXBException, FeedException {
        usagePoint = EspiMarshaller.<UsagePoint>unmarshal(XML_INPUT).getValue();
    }

    @Test
    public void UsagePoint() {
        assertEquals(UsagePoint.class, usagePoint.getClass());
    }

    @Test
    public void ServiceCategory() {
        assertEquals(ServiceCategory.class, usagePoint.getServiceCategory().getClass());
    }

    @Test
    public void ServiceCategory_Kind() {
        assertEquals(new Long(0L), usagePoint.getServiceCategory().getKind());
    }

}

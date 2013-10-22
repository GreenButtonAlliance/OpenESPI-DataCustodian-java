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

package org.energyos.espi.datacustodian.integration;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.energyos.espi.datacustodian.atom.XMLTest;
import org.energyos.espi.datacustodian.domain.ServiceCategory;
import org.energyos.espi.datacustodian.utils.factories.EspiFactory;
import org.energyos.espi.datacustodian.utils.factories.FixtureFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.xml.sax.SAXException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

import static junit.framework.Assert.assertEquals;
import static org.custommonkey.xmlunit.XMLAssert.assertXpathExists;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
public class ServiceCategoryUnmarshallerTests extends XMLTest {
    @Autowired
    @Qualifier("atomMarshaller")
    private Jaxb2Marshaller atomMarshaller;

    private ServiceCategory loadServiceCategory() throws DatatypeConfigurationException, IOException {
        String xml = FixtureFactory.loadFixture("/fixtures/ServiceCategory.xml");
        return (ServiceCategory)atomMarshaller.unmarshal(new StreamSource(new StringReader(xml)));
    }

    @Test
    public void kind() throws SAXException, IOException, XpathException, DatatypeConfigurationException {
        assertEquals(Long.valueOf(9L), loadServiceCategory().getKind());
    }
}

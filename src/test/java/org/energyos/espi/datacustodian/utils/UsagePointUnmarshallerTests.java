/*
 * Copyright 2013 EnergyOS.org
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package org.energyos.espi.datacustodian.utils;

import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.hsqldb.lib.StringInputStream;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;

import static junit.framework.Assert.assertEquals;

public class UsagePointUnmarshallerTests {

    @Test(expected = javax.xml.bind.UnmarshalException.class)
    public void givenInvalidInput_throwsAnException() throws JAXBException {
        UsagePointUnmarshaller unmarshaller  = new UsagePointUnmarshaller();

        unmarshaller.unmarshal(new StringInputStream(""));
    }

    @Test
    public void givenValidInput_returnsUsagePoint() throws JAXBException, FileNotFoundException {
        String XML =  "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<UsagePoint><title>Electric meter</title></UsagePoint>";
        UsagePointUnmarshaller unmarshaller  = new UsagePointUnmarshaller();
        ByteArrayInputStream input = new ByteArrayInputStream(XML.getBytes());

        UsagePoint usagePoint = unmarshaller.unmarshal(input);

        assertEquals("Electric meter", usagePoint.getTitle());
    }
}

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

package org.energyos.espi.datacustodian.service;

import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EspiMarshallerServiceTest {

    @Test
    public void marshal_with_marshallableObject_returnsValidXml() throws Exception {
        UsagePoint usagePoint = new UsagePoint();

        String xmlResult = "<UsagePoint xmlns=\"http://naesb.org/espi\"/>";
        assertEquals(xmlResult, EspiMarshallerService.marshal(usagePoint));
    }
}

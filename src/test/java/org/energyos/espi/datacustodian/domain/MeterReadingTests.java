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

import org.junit.Test;

import javax.xml.bind.annotation.XmlTransient;

import static org.energyos.espi.datacustodian.support.TestUtils.assertAnnotationPresent;

public class MeterReadingTests {

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
}

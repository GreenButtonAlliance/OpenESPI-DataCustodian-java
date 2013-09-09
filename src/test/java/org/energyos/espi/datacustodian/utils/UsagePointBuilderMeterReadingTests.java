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

import org.energyos.espi.datacustodian.domain.MeterReading;
import org.energyos.espi.datacustodian.domain.ReadingType;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.energyos.espi.datacustodian.models.atom.FeedType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.xml.bind.JAXBException;
import java.io.IOException;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
public class UsagePointBuilderMeterReadingTests {

    @Autowired
    private ATOMMarshaller marshaller;
    private MeterReading meterReading;

    @Before
    public void before() throws IOException, JAXBException {
        ClassPathResource sourceFile = new ClassPathResource("/fixtures/15minLP_15Days.xml");
        FeedType feedType = marshaller.unmarshal(sourceFile.getInputStream());

        UsagePointBuilder builder = new UsagePointBuilder();

        meterReading = builder.newUsagePoints(feedType).get(0).getMeterReadings().get(0);
    }

    @Test
    public void buildsMeterReading() {
        assertEquals(MeterReading.class, meterReading.getClass());
    }

    @Test
    public void setsUsagePoint() {
        assertEquals(UsagePoint.class, meterReading.getUsagePoint().getClass());
    }

    @Test
    public void setsDescription() {
        assertNotNull(meterReading.getDescription());
    }

    @Test
    public void buildsReadingType() {
        assertEquals(ReadingType.class, meterReading.getReadingType().getClass());
    }

    @Test
    public void setsReadingTypeDescription() {
        assertNotNull(meterReading.getReadingType().getDescription());
    }
}


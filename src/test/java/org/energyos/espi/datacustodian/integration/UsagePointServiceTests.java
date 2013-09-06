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

import org.energyos.espi.datacustodian.domain.MeterReading;
import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.energyos.espi.datacustodian.service.RetailCustomerService;
import org.energyos.espi.datacustodian.service.UsagePointService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
@Transactional
public class UsagePointServiceTests {
    @Autowired
    private UsagePointService usagePointService;

    @Autowired
    private RetailCustomerService retailCustomerService;

    @Test
    public void importUsagePoint_persistsMeterReadings() throws IOException, JAXBException {
        RetailCustomer retailCustomer = new RetailCustomer();
        retailCustomer.setFirstName("Kurt");
        retailCustomer.setLastName("Godel");
        retailCustomerService.persist(retailCustomer);

        ClassPathResource sourceFile = new ClassPathResource("/fixtures/15minLP_15Days.xml");
        usagePointService.importUsagePoint(retailCustomer, sourceFile.getInputStream());
        List<UsagePoint> usagePoints = usagePointService.findAllByRetailCustomer(retailCustomer);
        MeterReading meterReading = usagePoints.get(0).getMeterReadings().get(0);

        assertNotNull(meterReading.getId());
    }
}

package org.energyos.espi.datacustodian.integration.service;

import org.energyos.espi.datacustodian.domain.MeterReading;
import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.energyos.espi.datacustodian.service.RetailCustomerService;
import org.energyos.espi.datacustodian.service.UsagePointService;
import org.energyos.espi.datacustodian.utils.factories.EspiFactory;
import org.energyos.espi.datacustodian.utils.factories.FixtureFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/test-context.xml")
public class UsagePointServiceTests {
    @Autowired
    private UsagePointService usagePointService;
    @Autowired
    private RetailCustomerService retailCustomerService;

    @Test
    public void associateByUUID_nonExistentUsagePoint() {
        UUID uuid = UUID.randomUUID();
        RetailCustomer retailCustomer = EspiFactory.newRetailCustomer();
        retailCustomerService.persist(retailCustomer);

        usagePointService.associateByUUID(retailCustomer, uuid);

        assertNotNull(usagePointService.findByUUID(uuid));
        assertEquals(retailCustomer.getId(), usagePointService.findByUUID(uuid).getRetailCustomer().getId());
        assertTrue(usagePointService.findAllByRetailCustomer(retailCustomer).size() > 0);
    }

    @Test
    public void importUsagePoint_persistsMeterReadings() throws IOException, JAXBException {
        RetailCustomer retailCustomer = EspiFactory.newRetailCustomer();
        retailCustomerService.persist(retailCustomer);

        UsagePoint usagePoint = EspiFactory.newUsagePoint(retailCustomer);
        usagePointService.createOrReplaceByUUID(usagePoint);

        usagePointService.importUsagePoints(FixtureFactory.newUsagePointInputStream(UUID.randomUUID()));

        List<UsagePoint> usagePoints = usagePointService.findAllByRetailCustomer(retailCustomer);
        MeterReading meterReading = usagePoints.get(0).getMeterReadings().get(0);

        assertNotNull(meterReading.getId());
    }
}

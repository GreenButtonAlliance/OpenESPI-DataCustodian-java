package org.energyos.espi.datacustodian.utils;

import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.service.UsagePointService;
import org.energyos.espi.datacustodian.utils.factories.FixtureFactory;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.UUID;

public class TestUtils {
    private TestUtils() {}

    public static void importUsagePoint(UsagePointService usagePointService, RetailCustomer customer, UUID uuid) throws JAXBException, IOException {
        usagePointService.importUsagePoints(FixtureFactory.newUsagePointInputStream(uuid));
        usagePointService.associateByUUID(customer, uuid);
    }
}

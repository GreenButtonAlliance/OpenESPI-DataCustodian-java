package org.energyos.espi.datacustodian.utils;

import com.sun.syndication.feed.atom.Link;
import org.energyos.espi.datacustodian.atom.EspiEntry;
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

    public static <T extends EspiEntry<?>> String findRelatedHref(T entry, String type) {
        for (Link link : entry.getRelatedLinks()) {
            if (link.getHref().contains(type)) {
                return link.getHref();
            }
        }
        return null;
    }
}

package org.energyos.espi.datacustodian.integration.service;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.energyos.espi.common.domain.RetailCustomer;
import org.energyos.espi.common.domain.Subscription;
import org.energyos.espi.common.service.ImportService;
import org.energyos.espi.common.service.UsagePointService;
import org.energyos.espi.common.test.EspiPersistenceFactory;
import org.energyos.espi.datacustodian.domain.XMLTest;
import org.energyos.espi.datacustodian.service.ExportService;
import org.energyos.espi.datacustodian.utils.factories.FixtureFactory;
import org.energyos.espi.datacustodian.web.ExportFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathExists;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/test-context.xml")
@Transactional
public class ExportServiceTests extends XMLTest {
    @Autowired
    private ExportService exportService;
    @Autowired
    private EspiPersistenceFactory factory;
    @Autowired
    private ImportService importService;
    @Autowired
    private UsagePointService usagePointService;

    private ExportFilter exportFilter = new ExportFilter(new HashMap<String, String>());

    @Test
    public void export_exportsUsagePoint() throws IOException, ParserConfigurationException, SAXException, XpathException {
        Subscription subscription = factory.createSubscription();
        factory.createUsagePoint(subscription.getRetailCustomer());

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        exportService.exportSubscription(subscription.getHashedId(), os, exportFilter);


        assertXpathExists("/:feed/:entry/:content/espi:UsagePoint", os.toString());
    }

    @Test
    public void export_exportsUsagePointAfterUpload() throws IOException, ParserConfigurationException, SAXException, XpathException {
        UUID firstUUID = UUID.randomUUID();

        RetailCustomer retailCustomer = factory.createRetailCustomer();

        importService.importData(FixtureFactory.newFeedInputStream(firstUUID));

        usagePointService.associateByUUID(retailCustomer, firstUUID);

        Subscription subscription = factory.createSubscription(retailCustomer);

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        exportService.exportSubscription(subscription.getHashedId(), os, exportFilter);

        assertXpathExists("/:feed/:entry/:link[@rel]", os.toString());
    }
}

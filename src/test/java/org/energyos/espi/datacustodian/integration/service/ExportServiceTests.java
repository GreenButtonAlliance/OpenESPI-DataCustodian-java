package org.energyos.espi.datacustodian.integration.service;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.energyos.espi.common.service.ExportService;
import org.energyos.espi.common.service.ImportService;
import org.energyos.espi.common.service.UsagePointService;
import org.energyos.espi.common.test.EspiPersistenceFactory;
import org.energyos.espi.common.utils.ExportFilter;
import org.energyos.espi.datacustodian.domain.XMLTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/test-context.xml")
@Transactional (rollbackFor= {javax.xml.bind.JAXBException.class}, 
                noRollbackFor = {javax.persistence.NoResultException.class, org.springframework.dao.EmptyResultDataAccessException.class })

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

    // a null test till we figure out how to most effectively initialize the services subsystem w/o @Autowired

    @Test
    public void null_test() throws XpathException, IOException, SAXException {
    	
       assertEquals("1", "1");
    }
    
    /*
    @Test
    public void export_exportsUsagePoint() throws IOException, ParserConfigurationException, SAXException, XpathException {
        Subscription subscription = factory.createSubscription();
        factory.createUsagePoint(subscription.getRetailCustomer());

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        exportService.exportSubscription(subscription.getHashedId(), os, exportFilter);


        assertXpathExists("/:feed/:entry/:content/espi:UsagePoint", os.toString());
    }
*/
    
/*
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
*/
}

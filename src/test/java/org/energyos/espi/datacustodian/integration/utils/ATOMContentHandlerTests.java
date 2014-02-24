package org.energyos.espi.datacustodian.integration.utils;

import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.parsers.SAXParserFactory;

import org.energyos.espi.common.service.EntryProcessorService;
import org.energyos.espi.common.utils.ATOMContentHandler;
import org.energyos.espi.datacustodian.BaseTest;
import org.energyos.espi.datacustodian.utils.factories.FixtureFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
@Transactional (rollbackFor= {javax.xml.bind.JAXBException.class}, 
                noRollbackFor = {javax.persistence.NoResultException.class, org.springframework.dao.EmptyResultDataAccessException.class })

public class ATOMContentHandlerTests extends BaseTest {
    @Autowired
    @Qualifier("atomMarshaller")
    private Jaxb2Marshaller marshaller;
    
    @Autowired
    private EntryProcessorService entryProcessorService;

    @Test
    @Ignore
    public void processEnty() throws Exception {
        JAXBContext context = marshaller.getJaxbContext();

        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XMLReader reader = factory.newSAXParser().getXMLReader();
        // EntryProcessorServiceImpl procssor = mock(EntryProcessorServiceImpl.class);
        ATOMContentHandler atomContentHandler = new ATOMContentHandler(context, entryProcessorService);

        reader.setContentHandler(atomContentHandler);

        reader.parse(new InputSource(FixtureFactory.newUsagePointInputStream(UUID.randomUUID())));

        //verify(procssor).process(any(EntryType.class));
    }
}

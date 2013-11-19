package org.energyos.espi.datacustodian.integration.utils;

import org.energyos.espi.datacustodian.BaseTest;
import org.energyos.espi.datacustodian.models.atom.EntryType;
import org.energyos.espi.datacustodian.utils.ATOMContentHandler;
import org.energyos.espi.datacustodian.utils.EntryProcessor;
import org.energyos.espi.datacustodian.utils.factories.FixtureFactory;
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

import javax.xml.bind.JAXBContext;
import javax.xml.parsers.SAXParserFactory;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
@Transactional
public class ATOMContentHandlerTests extends BaseTest {
    @Autowired
    @Qualifier("atomMarshaller")
    private Jaxb2Marshaller marshaller;

    @Test
    public void processEnty() throws Exception {
        JAXBContext context = marshaller.getJaxbContext();

        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XMLReader reader = factory.newSAXParser().getXMLReader();
        EntryProcessor procssor = mock(EntryProcessor.class);
        ATOMContentHandler atomContentHandler = new ATOMContentHandler(context, procssor);

        reader.setContentHandler(atomContentHandler);

        reader.parse(new InputSource(FixtureFactory.newUsagePointInputStream(UUID.randomUUID())));

        verify(procssor).process(any(EntryType.class));
    }
}

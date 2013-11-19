package org.energyos.espi.datacustodian.utils;

import org.energyos.espi.datacustodian.BaseTest;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.energyos.espi.datacustodian.models.atom.EntryType;
import org.energyos.espi.datacustodian.service.ResourceService;
import org.energyos.espi.datacustodian.utils.factories.ATOMFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.xml.sax.SAXException;

import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
public class EntryProcessorTests extends BaseTest {
    @Test
    public void process() throws SAXException {
        ResourceLinker resourceLinker = mock(ResourceLinker.class);
        ResourceConverter resourceConverter = mock(ResourceConverter.class);
        ResourceService resourceService = mock(ResourceService.class);
        when(resourceService.findByUUID(any(UUID.class), any(Class.class))).thenThrow(EmptyResultDataAccessException.class);
        EntryProcessor entryProcessor = new EntryProcessor(resourceLinker, resourceConverter, resourceService);
        EntryType entry = ATOMFactory.newUsagePointEntryType();

        entryProcessor.process(entry);

        verify(resourceLinker).link(entry.getContent().getUsagePoint());
        verify(resourceConverter).convert(entry);
    }

    @Test
    public void link() throws SAXException {
        ResourceLinker resourceLinker = mock(ResourceLinker.class);
        ResourceConverter resourceConverter = mock(ResourceConverter.class);
        EntryProcessor entryProcessor = new EntryProcessor(resourceLinker, resourceConverter, null);
        UsagePoint usagePoint = new UsagePoint();

        entryProcessor.link(usagePoint);

        verify(resourceLinker).link(usagePoint);
    }

    @Test
    public void convert() throws SAXException {
        ResourceLinker resourceLinker = mock(ResourceLinker.class);
        ResourceConverter resourceConverter = mock(ResourceConverter.class);
        EntryProcessor entryProcessor = new EntryProcessor(resourceLinker, resourceConverter, null);
        EntryType entry = ATOMFactory.newUsagePointEntryType();

        entryProcessor.convert(entry);

        verify(resourceConverter).convert(entry);
    }
}

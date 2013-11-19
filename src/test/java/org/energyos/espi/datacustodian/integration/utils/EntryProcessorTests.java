package org.energyos.espi.datacustodian.integration.utils;

import org.energyos.espi.datacustodian.BaseTest;
import org.energyos.espi.datacustodian.domain.*;
import org.energyos.espi.datacustodian.models.atom.EntryType;
import org.energyos.espi.datacustodian.models.atom.LinkType;
import org.energyos.espi.datacustodian.service.ResourceService;
import org.energyos.espi.datacustodian.utils.EntryProcessor;
import org.energyos.espi.datacustodian.utils.ResourceConverter;
import org.energyos.espi.datacustodian.utils.ResourceLinker;
import org.energyos.espi.datacustodian.utils.factories.ATOMFactory;
import org.energyos.espi.datacustodian.utils.factories.EspiFactory;
import org.energyos.espi.datacustodian.utils.factories.EspiPersistenceFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
@Transactional
public class EntryProcessorTests extends BaseTest {
    @Autowired
    private ResourceLinker linker;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private EspiPersistenceFactory factory;
    private EntryProcessor processor;
    private EntryType entry;

    @Before
    public void before() throws Exception {
        processor = new EntryProcessor(linker, new ResourceConverter(), resourceService);

        entry = new EntryType();
        entry.setId("urn:uuid:F77FBF34-A09E-4EBC-9606-FF1A59A17CAE");
        entry.setTitle("title");
        entry.setPublished(ATOMFactory.newDateTimeType());
        entry.setUpdated(ATOMFactory.newDateTimeType());
        entry.getLinks().add(new LinkType(LinkType.RELATED, "related"));
        entry.getLinks().add(new LinkType(LinkType.SELF, "self"));
        entry.getLinks().add(new LinkType(LinkType.UP, "up"));
    }

    @Test
    public void process_givenUsagePointEntry() {
        UsagePoint usagePoint = new UsagePoint();
        usagePoint.setServiceCategory(new ServiceCategory(ServiceCategory.ELECTRICITY_SERVICE));
        entry.getContent().setUsagePoint(usagePoint);

        EntryType entryType = processor.process(entry);

        assertThat(entryType.getContent().getResource().getId(), is(notNullValue()));
    }

    @Test
    public void process_givenLocatTimeParametersEntry() {
        entry.getContent().setLocalTimeParameters(new TimeConfiguration());

        EntryType entryType = processor.process(entry);

        assertThat(entryType.getContent().getResource().getId(), is(notNullValue()));
    }

    @Test
    public void process_linksUsagePointAndMeterReading() {
        entry.getContent().setReadingType(new ReadingType());

        EntryType entryType = processor.process(entry);

        assertThat(entryType.getContent().getResource().getId(), is(notNullValue()));
    }

    @Test
    public void link_linkRelated() {
        String relatedLink = UUID.randomUUID().toString();
        MeterReading meterReading  = EspiFactory.newMeterReading();
        meterReading.setUpLink(new LinkType(LinkType.UP, relatedLink));
        resourceService.persist(meterReading);
        UsagePoint usagePoint = factory.createUsagePoint();
        usagePoint.getRelatedLinks().add(new LinkType(LinkType.RELATED, relatedLink));
        resourceService.persist(usagePoint);

        assertThat(meterReading.getUsagePoint(), is(nullValue()));

        linker.link(usagePoint);

        assertThat(meterReading.getUsagePoint(), equalTo(usagePoint));
    }

}

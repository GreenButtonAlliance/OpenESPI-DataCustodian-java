package org.energyos.espi.datacustodian.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.UUID;

import org.energyos.espi.common.domain.UsagePoint;
import org.energyos.espi.common.models.atom.EntryType;
import org.energyos.espi.common.models.atom.LinkType;
import org.energyos.espi.common.utils.ResourceConverter;
import org.energyos.espi.datacustodian.BaseTest;
import org.energyos.espi.datacustodian.utils.factories.ATOMFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
public class ResourceConverterTests extends BaseTest {

    private ResourceConverter converter;
    private EntryType usagePointEntry;

    @Before
    public void before() throws Exception {
        converter = new ResourceConverter();

        usagePointEntry = new EntryType();
        usagePointEntry.setId("urn:uuid:F77FBF34-A09E-4EBC-9606-FF1A59A17CAE");
        usagePointEntry.setTitle("title");
        usagePointEntry.getContent().setUsagePoint(new UsagePoint());
        usagePointEntry.setPublished(ATOMFactory.newDateTimeType());
        usagePointEntry.setUpdated(ATOMFactory.newDateTimeType());
        usagePointEntry.getLinks().add(new LinkType(LinkType.RELATED, "related"));
        usagePointEntry.getLinks().add(new LinkType(LinkType.SELF, "self"));
        usagePointEntry.getLinks().add(new LinkType(LinkType.UP, "up"));

        converter.convert(usagePointEntry);
    }

    @Test
    public void convert_setsId() throws Exception {
        assertThat(usagePointEntry.getContent().getResource().getUUID(), is(equalTo(UUID.fromString("F77FBF34-A09E-4EBC-9606-FF1A59A17CAE"))));
    }

    @Test
    public void convert_setsSelfLink() throws Exception {
        assertThat(usagePointEntry.getContent().getResource().getSelfLink(), is(equalTo(new LinkType(LinkType.SELF, "self"))));
    }

    @Test
    public void convert_setsUpLink() throws Exception {
        assertThat(usagePointEntry.getContent().getResource().getUpLink(), is(equalTo(new LinkType(LinkType.UP, "up"))));
    }

    @Test
    public void convert_setsRelatedLink() throws Exception {
        assertThat(usagePointEntry.getContent().getResource().getRelatedLinks(), hasItem(new LinkType(LinkType.RELATED, "related")));
    }

    @Test
    public void convert_setsTitle() throws Exception {
        assertThat(usagePointEntry.getContent().getResource().getDescription(), is("title"));
    }

    @Test
    public void convert_setsPublished() throws Exception {
        assertThat(usagePointEntry.getContent().getResource().getPublished(), is(notNullValue()));
    }

    @Test
    public void convert_setsUpdated() throws Exception {
        assertThat(usagePointEntry.getContent().getResource().getUpdated(), is(notNullValue()));
    }
}

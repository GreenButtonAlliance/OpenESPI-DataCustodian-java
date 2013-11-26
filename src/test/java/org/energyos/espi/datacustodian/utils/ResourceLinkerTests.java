package org.energyos.espi.datacustodian.utils;

import org.energyos.espi.common.domain.*;
import org.energyos.espi.common.models.atom.LinkType;
import org.energyos.espi.common.service.ResourceService;
import org.energyos.espi.common.utils.ResourceLinker;
import org.energyos.espi.datacustodian.BaseTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class ResourceLinkerTests extends BaseTest {
    @Mock
    private ResourceService resourceService;
    private ResourceLinker linker;
    private MeterReading meterReading;
    private UsagePoint usagePoint;
    private TimeConfiguration localTimeParameters;
    private ReadingType readingType;
    private IntervalBlock intervalBlock;
    private ElectricPowerQualitySummary electricPowerQualitySummary;
    private ElectricPowerUsageSummary electricPowerUsageSummary;

    @Before
    public void before() {
        linker = spy(new ResourceLinker());
        linker.setResourceService(resourceService);

        meterReading = new MeterReading();
        meterReading.setUpLink(new LinkType(LinkType.UP, "/espi/1_1/resource/RetailCustomer/9B6C7066/UsagePoint/5446AF3F/MeterReading"));
        usagePoint = new UsagePoint();
        usagePoint.setUpLink(new LinkType(LinkType.UP, "usagePointUpLink"));
        localTimeParameters = new TimeConfiguration();
        localTimeParameters.setSelfLink(new LinkType(LinkType.SELF, "/espi/1_1/resource/LocalTimeParameters/01"));
        readingType = new ReadingType();
        readingType.setSelfLink(new LinkType(LinkType.SELF, "/espi/1_1/resource/ReadingType/07"));
        intervalBlock = new IntervalBlock();
        intervalBlock.setUpLink(new LinkType(LinkType.UP, "/espi/1_1/resource/RetailCustomer/9B6C7066/UsagePoint/5446AF3F/MeterReading/01/IntervalBlock"));
        electricPowerQualitySummary = new ElectricPowerQualitySummary();
        electricPowerQualitySummary.setUpLink(new LinkType(LinkType.UP, "/espi/1_1/resource/RetailCustomer/9B6C7066/UsagePoint/5446AF3F/ElectricPowerQualitySummary"));
        electricPowerUsageSummary = new ElectricPowerUsageSummary();
        electricPowerUsageSummary.setUpLink(new LinkType(LinkType.UP, "/espi/1_1/resource/RetailCustomer/9B6C7066/UsagePoint/5446AF3F/ElectricPowerUsageSummary"));

        List<IdentifiedObject> related = new ArrayList<>();
        related.add(meterReading);

        when(resourceService.findAllRelated(eq(usagePoint))).thenReturn(related);
        List<IdentifiedObject> parents = newList(usagePoint);
        when(resourceService.findByAllParentsHref(eq("/espi/1_1/resource/RetailCustomer/9B6C7066/UsagePoint/5446AF3F/MeterReading"), eq(meterReading))).thenReturn(parents);
        when(resourceService.findByAllParentsHref(eq("/espi/1_1/resource/LocalTimeParameters/01"), eq(localTimeParameters))).thenReturn(parents);
        when(resourceService.findByAllParentsHref(eq("/espi/1_1/resource/ReadingType/07"), eq(readingType))).thenReturn(newList(meterReading));
        when(resourceService.findByAllParentsHref(eq("/espi/1_1/resource/RetailCustomer/9B6C7066/UsagePoint/5446AF3F/MeterReading/01/IntervalBlock"),
                eq(intervalBlock))).thenReturn(newList(meterReading));
        when(resourceService.findByAllParentsHref(eq("/espi/1_1/resource/RetailCustomer/9B6C7066/UsagePoint/5446AF3F/ElectricPowerQualitySummary"),
                eq(electricPowerQualitySummary))).thenReturn(parents);
        when(resourceService.findByAllParentsHref(eq("/espi/1_1/resource/RetailCustomer/9B6C7066/UsagePoint/5446AF3F/ElectricPowerUsageSummary"),
                eq(electricPowerUsageSummary))).thenReturn(parents);
    }

    private List<IdentifiedObject> newList(IdentifiedObject ... objs) {
        List<IdentifiedObject> list = new ArrayList<>();
        for(IdentifiedObject obj : objs) {
            list.add(obj);
        }
        return list;
    }

    @Test
    public void link_persistsResource() throws SAXException {
        linker.link(usagePoint);

        verify(resourceService).persist(eq(usagePoint));
    }

    @Test
    public void linkUp_givenMeterReading() {
        linker.linkUp(meterReading);

        assertThat(meterReading.getUsagePoint(), equalTo(usagePoint));
        assertThat(usagePoint.getMeterReadings().get(0), equalTo(meterReading));
    }

    @Test
    public void linkUp_givenIntervalBlock() {
        linker.linkUp(intervalBlock);

        assertThat(intervalBlock.getMeterReading(), equalTo(meterReading));
        assertThat(meterReading.getIntervalBlocks().get(0), equalTo(intervalBlock));
    }

    @Test
    public void linkUp_givenElectricPowerQualitySummary() {
        linker.linkUp(electricPowerQualitySummary);

        assertThat(electricPowerQualitySummary.getUsagePoint(), equalTo(usagePoint));
        assertThat(usagePoint.getElectricPowerQualitySummaries().get(0), equalTo(electricPowerQualitySummary));
    }

    @Test
    public void linkUp_givenElectricPowerUsageSummary() {
        linker.linkUp(electricPowerUsageSummary);

        assertThat(electricPowerUsageSummary.getUsagePoint(), equalTo(usagePoint));
        assertThat(usagePoint.getElectricPowerUsageSummaries().get(0), equalTo(electricPowerUsageSummary));
    }

    @Test
    public void linkUpMember_givenLocalTimeParameters() {
        linker.linkUpMember(localTimeParameters);

        assertThat(usagePoint.getLocalTimeParameters(), equalTo(localTimeParameters));
    }

    @Test
    public void linkUpMember_givenLocalTimeParameters_persistsParentResource() {
        linker.linkUpMember(localTimeParameters);

        verify(resourceService).persist(eq(usagePoint));
    }

    @Test
    public void linkUpMember_givenReadingType() {
        linker.linkUpMember(readingType);

        assertThat(meterReading.getReadingType(), equalTo(readingType));
    }

    @Test
    public void linkUpMember_givenReadingType_persistsParentResource() {
        linker.linkUpMember(readingType);

        verify(resourceService).persist(eq(meterReading));
    }

    @Test
    public void linkRelatedCollection_setsUpResource() {
        linker.linkRelatedCollection(usagePoint);

        assertThat(meterReading.getUsagePoint(), equalTo(usagePoint));
        assertThat(usagePoint.getMeterReadings().get(0), equalTo(meterReading));
    }

    @Test
    public void linkRelatedCollection_persistsRelatedResource() {
        linker.linkRelatedCollection(usagePoint);

        verify(resourceService).persist(eq(meterReading));
    }
}

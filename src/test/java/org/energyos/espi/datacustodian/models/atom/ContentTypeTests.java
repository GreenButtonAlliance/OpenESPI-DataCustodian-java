package org.energyos.espi.datacustodian.models.atom;

import org.energyos.espi.datacustodian.domain.*;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

public class ContentTypeTests {
    @Test
    public void getResource_returnsUsagePoint() throws Exception {
        ContentType content = new ContentType();
        content.setUsagePoint(new UsagePoint());

        assertThat(content.getResource(), instanceOf(UsagePoint.class));
    }

    @Test
    public void getResource_returnsMeterReading() throws Exception {
        ContentType content = new ContentType();
        content.setMeterReading(new MeterReading());

        assertThat(content.getResource(), instanceOf(MeterReading.class));
    }

    @Test
    public void getResource_returnsTimeConfiguration() {
        ContentType content = new ContentType();
        content.setLocalTimeParameters(new TimeConfiguration());

        assertThat(content.getResource(), instanceOf((TimeConfiguration.class)));
    }

    @Test
    public void getResource_returnsElectricPowerUsageSummary() {
        ContentType content = new ContentType();
        content.setElectricPowerUsageSummary(new ElectricPowerUsageSummary());

        assertThat(content.getResource(), instanceOf((ElectricPowerUsageSummary.class)));
    }

    @Test
    public void getResource_returnsElectricPowerQualitySummary() {
        ContentType content = new ContentType();
        content.setElectricPowerQualitySummary(new ElectricPowerQualitySummary());

        assertThat(content.getResource(), instanceOf((ElectricPowerQualitySummary.class)));
    }

    @Test
    public void getResource_returnsReadingType() {
        ContentType content = new ContentType();
        content.setReadingType(new ReadingType());

        assertThat(content.getResource(), instanceOf((ReadingType.class)));
    }
}

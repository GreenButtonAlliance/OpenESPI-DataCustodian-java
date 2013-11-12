package org.energyos.espi.datacustodian.models.atom;

import org.energyos.espi.datacustodian.domain.MeterReading;
import org.energyos.espi.datacustodian.domain.UsagePoint;
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
}

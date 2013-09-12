package org.energyos.espi.datacustodian.utils.factories;

import org.energyos.espi.datacustodian.domain.*;

import java.util.GregorianCalendar;

public class Factory {

    public static UsagePoint newUsagePoint() {
        UsagePoint usagePoint = new UsagePoint();
        usagePoint.setId(99L);

        RetailCustomer retailCustomer = new RetailCustomer();
        retailCustomer.setId(88L);
        usagePoint.setRetailCustomer(retailCustomer);

        ElectricPowerUsageSummary electricPowerUsageSummary = newElectricPowerUsageSummary(usagePoint);
        usagePoint.addElectricPowerUsageSummary(electricPowerUsageSummary);

        return usagePoint;
    }

    public static ElectricPowerUsageSummary newElectricPowerUsageSummary(UsagePoint usagePoint) {
        ElectricPowerUsageSummary summary = new ElectricPowerUsageSummary();
        summary.setId(1L);
        summary.setMRID("DEB0A337-B1B5-4658-99CA-4688E253A99B");
        summary.setUsagePoint(usagePoint);
        summary.setDescription("Usage Summary");
        summary.setBillingPeriod(new DateTimeInterval(1119600L, 1119600L));
        summary.setCreated(new GregorianCalendar(2012, 10, 24, 0, 0, 0).getTime());
        summary.setUpdated(new GregorianCalendar(2012, 10, 24, 0, 0, 0).getTime());
        summary.setBillLastPeriod(15303000L);
        summary.setBillToDate(1135000L);
        summary.setCostAdditionalLastPeriod(1346000L);
        summary.setCurrency("840");

        SummaryMeasurement summaryMeasurement = new SummaryMeasurement("0", 1331784000L, "72", 93018L);
        summary.setCurrentBillingPeriodOverAllConsumption(summaryMeasurement);
        summary.setQualityOfReading("14");
        summary.setStatusTimeStamp(1331784000L);
        summary.setCurrentDayLastYearNetConsumption(summaryMeasurement);
        summary.setCurrentDayNetConsumption(summaryMeasurement);
        summary.setCurrentDayOverallConsumption(summaryMeasurement);
        summary.setPeakDemand(summaryMeasurement);
        summary.setPreviousDayLastYearOverallConsumption(summaryMeasurement);
        summary.setPreviousDayNetConsumption(summaryMeasurement);
        summary.setPreviousDayOverallConsumption(summaryMeasurement);
        summary.setRatchetDemand(summaryMeasurement);
        summary.setRatchetDemandPeriod(new DateTimeInterval(1119600L, 1119600L));

        return summary;
    }
}

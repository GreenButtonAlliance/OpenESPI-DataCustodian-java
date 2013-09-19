package org.energyos.espi.datacustodian.utils.factories;

import org.energyos.espi.datacustodian.domain.*;

import java.math.BigInteger;
import java.util.GregorianCalendar;

public class EspiFactory {

    public static UsagePoint newUsagePoint() {
        return _usagePoint();
    }

    public static MeterReading newMeterReading() {
        return _usagePoint().getMeterReadings().get(0);
    }

    public static ReadingType newReadingType() {
        return _readingType();
    }

    public static ElectricPowerUsageSummary newElectricPowerUsageSummary() {
        return _usagePoint().getElectricPowerUsageSummaries().get(0);
    }

    public static IntervalBlock newIntervalBlock() {
        return _usagePoint().getMeterReadings().get(0).getIntervalBlocks().get(0);
    }

    public static IntervalReading newIntervalReading() {
        return _usagePoint().getMeterReadings().get(0).getIntervalBlocks().get(0).getIntervalReadings().get(0);
    }

    private static UsagePoint _usagePoint() {
        UsagePoint usagePoint = new UsagePoint();

        usagePoint.setId(99L);
        usagePoint.setMRID("7BC41774-7190-4864-841C-861AC76D46C2");
        usagePoint.setDescription("Electric meter");

        usagePoint.setRoleFlags("role flags".getBytes());
        usagePoint.setStatus(new Short("5"));

        ServiceCategory serviceCategory = new ServiceCategory();
        serviceCategory.setKind(0L);

        usagePoint.setServiceCategory(serviceCategory);

        usagePoint.setRetailCustomer(_retailCustomer());
        usagePoint.addMeterReading(_meterReading());
        usagePoint.addElectricPowerUsageSummary(_electricPowerUsageSummary());

        return usagePoint;
    }

    private static RetailCustomer _retailCustomer() {
        RetailCustomer retailCustomer = new RetailCustomer();
        retailCustomer.setId(88L);

        return retailCustomer;
    }

    private static MeterReading _meterReading() {
        MeterReading meterReading = new MeterReading();

        meterReading.setId(98L);
        meterReading.setMRID("E8B19EF0-6833-41CE-A28B-A5E7F9F193AE");
        meterReading.setDescription("Electricity consumption");

        meterReading.addIntervalBlock(_intervalBlock());
        meterReading.addIntervalBlock(_intervalBlock());
        meterReading.addIntervalBlock(_intervalBlock());

        meterReading.setReadingType(_readingType());

        return meterReading;
    }

    private static ReadingType _readingType() {
        ReadingType readingType = new ReadingType();

        readingType.setId(96L);
        readingType.setDescription("Energy Delivered (kWh)");
        readingType.setMRID("82B3E74B-DFC0-4DD4-8651-91A67B40374D");

        RationalNumber argument = new RationalNumber();
        argument.setNumerator(new BigInteger("1"));
        argument.setDenominator(new BigInteger("3"));

        ReadingInterharmonic interharmonic = new ReadingInterharmonic();
        interharmonic.setNumerator(new BigInteger("1"));
        interharmonic.setDenominator(new BigInteger("6"));

        readingType.setAccumulationBehaviour("accumulationBehaviour");
        readingType.setCommodity("commodity");
        readingType.setDataQualifier("dataQualifier");
        readingType.setFlowDirection("flowDirection");
        readingType.setIntervalLength(10L);
        readingType.setKind("kind");
        readingType.setPhase("phase");
        readingType.setPowerOfTenMultiplier("multiplier");
        readingType.setTimeAttribute("timeAttribute");
        readingType.setUom("uom");
        readingType.setConsumptionTier("consumptionTier");
        readingType.setCpp("cpp");
        readingType.setCurrency("currency");
        readingType.setTou("tou");
        readingType.setAggregate("aggregate");
        readingType.setArgument(argument);
        readingType.setInterharmonic(interharmonic);
        readingType.setMeasuringPeriod("measuringPeriod");

        return readingType;
    }

    private static IntervalBlock _intervalBlock() {
        IntervalBlock intervalBlock = new IntervalBlock();

        DateTimeInterval interval = new DateTimeInterval();
        interval.setDuration(86400L);
        interval.setStart(1330578000L);

        intervalBlock.addIntervalReading(_intervalReading());
        intervalBlock.addIntervalReading(_intervalReading());

        intervalBlock.setId(97L);
        intervalBlock.setMRID("E8E75691-7F9D-49F3-8BE2-3A74EBF6BFC0");
        intervalBlock.setInterval(interval);

        return intervalBlock;
    }

    private static IntervalReading _intervalReading() {
        IntervalReading intervalReading = new IntervalReading();

        DateTimeInterval timePeriod = new DateTimeInterval();
        timePeriod.setDuration(86401L);
        timePeriod.setStart(1330578001L);

        intervalReading.setCost(100L);
        intervalReading.setValue(6L);

        intervalReading.addReadingQuality(_readingQuality("quality1"));
        intervalReading.addReadingQuality(_readingQuality("quality2"));

        intervalReading.setId(96L);
        intervalReading.setMRID("E8E75691-7F9D-49F3-8BE2-3A74EBF6BFC0");

        intervalReading.setTimePeriod(timePeriod);


        return intervalReading;
    }

    private static ReadingQuality _readingQuality(String quality) {
        ReadingQuality readingQuality = new ReadingQuality();
        readingQuality.setQuality(quality);

        return readingQuality;
    }

    private static ElectricPowerUsageSummary _electricPowerUsageSummary() {
        ElectricPowerUsageSummary summary = new ElectricPowerUsageSummary();

        summary.setId(1L);
        summary.setMRID("DEB0A337-B1B5-4658-99CA-4688E253A99B");
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

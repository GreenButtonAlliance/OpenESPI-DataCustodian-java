package org.energyos.espi.datacustodian.utils.factories;

import org.energyos.espi.datacustodian.domain.*;

import java.math.BigInteger;
import java.util.GregorianCalendar;
import java.util.UUID;

public class EspiFactory {

    public static UsagePoint newUsagePointOnly(UUID uuid) {
        UsagePoint usagePoint = new UsagePoint();

        usagePoint.setUUID(uuid);
        usagePoint.setDescription("Electric meter");

        usagePoint.setServiceCategory(new ServiceCategory(ServiceCategory.ELECTRICITY_SERVICE));

        return usagePoint;
    }

    public static UsagePoint newUsagePoint() {
       return newUsagePoint(newRetailCustomer());
    }

    public static UsagePoint newUsagePointWithId(RetailCustomer retailCustomer) {
        UsagePoint usagePoint = newUsagePoint(retailCustomer);
        usagePoint.setId(2L);
        usagePoint.getLocalTimeParameters().setId(1L);

        return usagePoint;
    }

    public static UsagePoint newUsagePointWithId() {
        return newUsagePointWithId(newRetailCustomerWithId());
    }

    private static RetailCustomer newRetailCustomerWithId() {
        RetailCustomer retailCustomer = newRetailCustomer();
        retailCustomer.setId(1L);
        return retailCustomer;
    }

    public static UsagePoint newUsagePoint(RetailCustomer retailCustomer) {
        UsagePoint usagePoint = new UsagePoint();

        usagePoint.setUUID(UUID.randomUUID());
        usagePoint.setDescription("Electric meter");

        usagePoint.setRoleFlags("role flags".getBytes());
        usagePoint.setStatus(new Short("5"));

        usagePoint.setServiceCategory(new ServiceCategory(ServiceCategory.ELECTRICITY_SERVICE));

        usagePoint.setRetailCustomer(retailCustomer);
        usagePoint.addMeterReading(newMeterReading());
        usagePoint.addElectricPowerUsageSummary(newElectricPowerUsageSummary());
        usagePoint.addElectricPowerQualitySummary(newElectricPowerQualitySummary());
        usagePoint.setLocalTimeParameters(newLocalTimeParameters());
        usagePoint.setCreated(new GregorianCalendar(2012, 10, 15, 0, 0, 0).getTime());
        usagePoint.setUpdated(new GregorianCalendar(2012, 10, 17, 0, 0, 0).getTime());

        return usagePoint;
    }

    public static MeterReading newMeterReadingWithUsagePoint() {
        return newUsagePoint().getMeterReadings().get(0);
    }

    public static ElectricPowerUsageSummary newElectricPowerUsageSummaryWithUsagePoint() {
        return newUsagePoint().getElectricPowerUsageSummaries().get(0);
    }

    public static ElectricPowerQualitySummary newElectricPowerQualitySummaryWithUsagePoint() {
        return newUsagePoint().getElectricPowerQualitySummaries().get(0);
    }

    public static TimeConfiguration newTimeConfigurationWithUsagePoint() {
        return newUsagePoint().getLocalTimeParameters();
    }

    public static IntervalBlock newIntervalBlockWithUsagePoint() {
        return newUsagePoint().getMeterReadings().get(0).getIntervalBlocks().get(0);
    }

    public static RetailCustomer newRetailCustomer() {
        RetailCustomer retailCustomer = new RetailCustomer();
        retailCustomer.setFirstName("First" + System.currentTimeMillis());
        retailCustomer.setLastName("Last" + System.currentTimeMillis());

        return retailCustomer;
    }

    private static MeterReading newMeterReading() {
        MeterReading meterReading = new MeterReading();

        meterReading.setMRID("E8B19EF0-6833-41CE-A28B-A5E7F9F193AE");
        meterReading.setDescription("Electricity consumption");

        meterReading.addIntervalBlock(newIntervalBlock());
        meterReading.addIntervalBlock(newIntervalBlock());
        meterReading.addIntervalBlock(newIntervalBlock());

        meterReading.setReadingType(newReadingType());

        return meterReading;
    }

    private static TimeConfiguration newLocalTimeParameters() {
        TimeConfiguration timeConfiguration = new TimeConfiguration();

        timeConfiguration.setDescription("DST For North America");
        timeConfiguration.setMRID("54C62EBE-2DB6-4D4F-B6BF-1973A079C841");

        timeConfiguration.setDstEndRule("foo".getBytes());
        timeConfiguration.setDstOffset(1000L);
        timeConfiguration.setDstStartRule("bar".getBytes());
        timeConfiguration.setTzOffset(1234L);

        return timeConfiguration;
    }

    public static ReadingType newReadingType() {
        ReadingType readingType = new ReadingType();

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

    private static IntervalBlock newIntervalBlock() {
        IntervalBlock intervalBlock = new IntervalBlock();

        DateTimeInterval interval = new DateTimeInterval();
        interval.setDuration(86400L);
        interval.setStart(1330578000L);

        intervalBlock.addIntervalReading(newIntervalReading());
        intervalBlock.addIntervalReading(newIntervalReading());

        intervalBlock.setMRID("E8E75691-7F9D-49F3-8BE2-3A74EBF6BFC0");
        intervalBlock.setInterval(interval);

        return intervalBlock;
    }

    public static IntervalReading newIntervalReading() {
        IntervalReading intervalReading = new IntervalReading();

        DateTimeInterval timePeriod = new DateTimeInterval();
        timePeriod.setDuration(86401L);
        timePeriod.setStart(1330578001L);

        intervalReading.setCost(100L);
        intervalReading.setValue(6L);

        intervalReading.addReadingQuality(_readingQuality("quality1"));
        intervalReading.addReadingQuality(_readingQuality("quality2"));

        intervalReading.setMRID("E8E75691-7F9D-49F3-8BE2-3A74EBF6BFC0");

        intervalReading.setTimePeriod(timePeriod);


        return intervalReading;
    }

    private static ReadingQuality _readingQuality(String quality) {
        ReadingQuality readingQuality = new ReadingQuality();
        readingQuality.setQuality(quality);

        return readingQuality;
    }

    private static ElectricPowerUsageSummary newElectricPowerUsageSummary() {
        ElectricPowerUsageSummary summary = new ElectricPowerUsageSummary();

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

    private static ElectricPowerQualitySummary newElectricPowerQualitySummary() {
        ElectricPowerQualitySummary summary = new ElectricPowerQualitySummary();

        summary.setMRID("DEB0A337-C1B5-4658-99BA-4688E253A99B");
        summary.setDescription("Quality Summary");
        summary.setFlickerPlt(1L);
        summary.setFlickerPst(2L);
        summary.setHarmonicVoltage(3L);
        summary.setLongInterruptions(4L);
        summary.setMainsVoltage(5L);
        summary.setMeasurementProtocol((short) 6);
        summary.setPowerFrequency(7L);
        summary.setRapidVoltageChanges(8L);
        summary.setShortInterruptions(9L);

        DateTimeInterval summaryInterval = new DateTimeInterval();
        summaryInterval.setDuration(2119600L);
        summaryInterval.setStart(2330578000L);

        summary.setSummaryInterval(summaryInterval);
        summary.setSupplyVoltageDips(10L);
        summary.setSupplyVoltageImbalance(11L);
        summary.setSupplyVoltageVariations(12L);
        summary.setTempOvervoltage(13L);

        return summary;
    }

    public static ThirdParty newThirdParty() {
        ThirdParty thirdParty = new ThirdParty();
        thirdParty.setName("Name" + System.currentTimeMillis());
        thirdParty.setClientId("Client" + System.currentTimeMillis());

        return thirdParty;
    }

    public static Subscription newSubscription(RetailCustomer retailCustomer) {
        Subscription subscription = new Subscription();
        subscription.setUUID(UUID.randomUUID());
        subscription.setRetailCustomer(retailCustomer);
        return subscription;
    }

    public static Authorization newAuthorization(Subscription subscription) {
        Authorization authorization = new Authorization();
        authorization.setUUID(UUID.randomUUID());
        authorization.setAccessToken(UUID.randomUUID().toString());
        authorization.setResource("/Resource/" + UUID.randomUUID().toString());
        return authorization;
    }
}

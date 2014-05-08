package features.steps;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathExists;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyLong;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.energyos.espi.common.domain.ElectricPowerUsageSummary;
import org.energyos.espi.common.domain.IntervalBlock;
import org.energyos.espi.common.domain.MeterReading;
import org.energyos.espi.common.domain.RetailCustomer;
import org.energyos.espi.common.domain.UsagePoint;
import org.energyos.espi.common.service.ExportService;
import org.energyos.espi.common.service.ImportService;
import org.energyos.espi.common.service.RetailCustomerService;
import org.energyos.espi.common.service.UsagePointService;
import org.energyos.espi.common.utils.ExportFilter;
import org.energyos.espi.datacustodian.utils.factories.EspiFactory;
import org.energyos.espi.datacustodian.utils.factories.FixtureFactory;
import org.junit.Ignore;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cucumber.api.java.Before;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class IntegrationSteps {

    private ApplicationContext ctx;
    private UsagePointService usagePointService;
    private ImportService importService;
    private RetailCustomerService retailCustomerService;
    private RetailCustomer retailCustomer;
    private ExportService exportService;
    private String xml;

    @Before("@spring")
    public void before() {
        ctx = new ClassPathXmlApplicationContext("/spring/test-service-context.xml");
        usagePointService = ctx.getBean(UsagePointService.class);
        retailCustomerService = ctx.getBean(RetailCustomerService.class);
        importService = ctx.getBean(ImportService.class);
        exportService = ctx.getBean(ExportService.class);
    }

    @When("^I import Usage Point$")
    @Ignore
    public void I_import_Usage_Point() throws Throwable {
        retailCustomer = EspiFactory.newRetailCustomer();
        retailCustomerService.persist(retailCustomer);

        UsagePoint usagePoint = EspiFactory.newUsagePoint(retailCustomer);
        usagePointService.createOrReplaceByUUID(usagePoint);

        importService.importData(FixtureFactory.newFeedInputStream(UUID.randomUUID()), null);
    }

    @When("^I export Usage Point$")
    @Ignore
    public void I_export_Usage_Point() throws Throwable {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        exportService.exportUsagePoints(anyLong(), retailCustomer.getId(), os, new ExportFilter(new HashMap<String, String>()));
        xml = os.toString();
    }

    @Then("^Usage Point should be saved in the database$")
    @Ignore
    public void Usage_Point_should_be_saved_in_the_database() throws Throwable {
        List<UsagePoint> usagePoints = usagePointService.findAllByRetailCustomer(retailCustomer);
        assertEquals("Usage point not saved", 1, usagePoints.size());
    }

    @Then("^Meter Readings should be saved in the database$")
    @Ignore
    public void Usage_Point_s_Meter_Readings_should_be_saved_in_the_database() throws Throwable {
        List<UsagePoint> usagePoints = usagePointService.findAllByRetailCustomer(retailCustomer);
        MeterReading meterReading = usagePoints.get(0).getMeterReadings().get(0);
        assertNotNull("Meter reading not saved", meterReading);
    }

    @Then("^Interval Blocks and Readings should be saved in the database$")
    @Ignore
    public void Interval_Blocks_should_be_saved_in_the_database() throws Throwable {
        List<UsagePoint> usagePoints = usagePointService.findAllByRetailCustomer(retailCustomer);
        MeterReading meterReading = usagePoints.get(0).getMeterReadings().get(0);
        IntervalBlock intervalBlock = meterReading.getIntervalBlocks().get(0);
        assertNotNull(intervalBlock);
        assertNotNull(intervalBlock.getIntervalReadings().get(0));
    }

    @Then("^export data should include Feed$")
    @Ignore
    public void export_data_should_include_Feed() throws Throwable {
        assertXpathExists("/:feed", xml);
        assertXpathExists("//espi:ElectricPowerUsageSummary", xml);
    }

    @When("^I create a Retail Customer$")
    public void I_create_a_Retail_Customer() throws Throwable {
        retailCustomer = EspiFactory.newRetailCustomer();
        retailCustomerService.persist(retailCustomer);
    }

    @Then("^a Retail Customer should be created$")
    public void a_Retail_Customer_should_be_created() throws Throwable {
        RetailCustomer createdRetailCustomer = retailCustomerService.findById(retailCustomer.getId());

        assertNotNull(createdRetailCustomer);
    }

    @Then("^Reading Type should be saved in the database$")
    @Ignore
    public void Reading_Type_should_be_saved_in_the_database() throws Throwable {
        List<UsagePoint> usagePoints = usagePointService.findAllByRetailCustomer(retailCustomer);
        MeterReading meterReading = usagePoints.get(0).getMeterReadings().get(0);
        assertNotNull(meterReading.getReadingType());
    }

    @Then("^Electric Power Usage Summary should be saved in the database$")
    @Ignore
    public void Electric_Power_Usage_Summary_should_be_saved_in_the_database() throws Throwable {
        List<UsagePoint> usagePoints = usagePointService.findAllByRetailCustomer(retailCustomer);
        ElectricPowerUsageSummary electricPowerUsageSummary = usagePoints.get(0).getElectricPowerUsageSummaries().get(0);
        assertNotNull("Electric Power Usage Summary was not saved", electricPowerUsageSummary);
    }
}

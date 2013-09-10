package features.steps;

import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.energyos.espi.datacustodian.domain.*;
import org.energyos.espi.datacustodian.service.RetailCustomerService;
import org.energyos.espi.datacustodian.service.UsagePointService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

import java.util.List;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathExists;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class IntegrationSteps {

    private ApplicationContext ctx;
    private UsagePointService usagePointService;
    private RetailCustomerService retailCustomerService;
    private RetailCustomer retailCustomer;
    private String xml;

    @Before
    public void before() {
        ctx = new ClassPathXmlApplicationContext("/spring/test-service-context.xml");
        usagePointService = ctx.getBean(UsagePointService.class);
        retailCustomerService = ctx.getBean(RetailCustomerService.class);
    }

    @When("^I import Usage Point$")
    public void I_import_Usage_Point() throws Throwable {
        retailCustomer = new RetailCustomer();
        retailCustomer.setFirstName("Kurt");
        retailCustomer.setLastName("Godel");
        retailCustomerService.persist(retailCustomer);

        ClassPathResource sourceFile = new ClassPathResource("/fixtures/15minLP_15Days.xml");

        usagePointService.importUsagePoints(retailCustomer, sourceFile.getInputStream());
    }

    @And("^I export Usage Point$")
    public void I_export_Usage_Point() throws Throwable {
        xml = usagePointService.exportUsagePoints(retailCustomer);
    }

    @Then("^Usage Point should be saved in the database$")
    public void Usage_Point_should_be_saved_in_the_database() throws Throwable {
        List<UsagePoint> usagePoints = usagePointService.findAllByRetailCustomer(retailCustomer);
        assertEquals("Usage point not saved", 1, usagePoints.size());
    }

    @And("^Meter Readings should be saved in the database$")
    public void Usage_Point_s_Meter_Readings_should_be_saved_in_the_database() throws Throwable {
        List<UsagePoint> usagePoints = usagePointService.findAllByRetailCustomer(retailCustomer);
        MeterReading meterReading = usagePoints.get(0).getMeterReadings().get(0);
        assertNotNull("Meter reading not saved", meterReading);
    }

    @And("^Interval Blocks should be saved in the database$")
    public void Interval_Blocks_should_be_saved_in_the_database() throws Throwable {
        List<UsagePoint> usagePoints = usagePointService.findAllByRetailCustomer(retailCustomer);
        MeterReading meterReading = usagePoints.get(0).getMeterReadings().get(0);
        IntervalBlock intervalBlock = meterReading.getIntervalBlocks().get(0);
        assertNotNull(intervalBlock);
    }

    @Then("^export data should include Feed$")
    public void export_data_should_include_Feed() throws Throwable {
        assertXpathExists("/feed", xml);
    }

    @When("^I create a Retail Customer$")
    public void I_create_a_Retail_Customer() throws Throwable {
        retailCustomer = new RetailCustomer();
        retailCustomer.setUsername("kurt");
        retailCustomer.setFirstName("Kurt");
        retailCustomer.setLastName("Godel");
        retailCustomer.setPassword("koala");
        retailCustomer.setRole(RetailCustomer.ROLE_CUSTOMER);
        retailCustomerService.persist(retailCustomer);
    }

    @Then("^a Retail Customer should be created$")
    public void a_Retail_Customer_should_be_created() throws Throwable {
        RetailCustomer createdRetailCustomer = retailCustomerService.findById(retailCustomer.getId());

        assertNotNull(createdRetailCustomer);
    }

    @And("^Reading Type should be saved in the database$")
    public void Reading_Type_should_be_saved_in_the_database() throws Throwable {
        List<UsagePoint> usagePoints = usagePointService.findAllByRetailCustomer(retailCustomer);
        MeterReading meterReading = usagePoints.get(0).getMeterReadings().get(0);
        assertNotNull(meterReading.getReadingType());
    }

    @And("^Electric Power Usage Summary should be saved in the database$")
    public void Electric_Power_Usage_Summary_should_be_saved_in_the_database() throws Throwable {
        List<UsagePoint> usagePoints = usagePointService.findAllByRetailCustomer(retailCustomer);
        ElectricPowerUsageSummary electricPowerUsageSummary = usagePoints.get(0).getElectricPowerUsageSummaries().get(0);
        assertNotNull("Electric Power Usage Summary was not saved", electricPowerUsageSummary);
    }
}

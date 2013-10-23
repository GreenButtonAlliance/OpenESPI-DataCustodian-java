package features.steps;

import cucumber.api.java.Before;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openqa.selenium.WebDriver;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathExists;

public class APISteps {

    private WebDriver driver;

    @Before
    public void setup() {
        driver = WebDriverSingleton.getInstance();
    }

    @When("^I GET \\/espi\\/1_1\\/resource\\/RetailCustomer\\/\\{RetailCustomerID\\}\\/UsagePoint$")
    public void I_GET_espi_1_1_resource_RetailCustomer_RetailCustomerID_UsagePoint() throws Throwable {
        driver.get(StepUtils.BASE_URL + "/espi/1_1/resource/RetailCustomer/1/UsagePoint");
    }

    @Then("^I should receive the list of Usage Points$")
    public void I_should_receive_the_list_of_Usage_Points() throws Throwable {
        assertXpathExists("/:feed/:entry[1]/:content/espi:UsagePoint", driver.getPageSource());
        assertXpathExists("/:feed/:entry[2]/:content/espi:UsagePoint", driver.getPageSource());
    }

}

package features.steps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openqa.selenium.WebDriver;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathExists;
import static org.energyos.espi.common.test.Asserts.assertXpathValue;

public class SubscriptionSteps {
    private WebDriver driver = WebDriverSingleton.getInstance();

    @Given("^an authorized Third Party$")
    public void an_authorized_Third_Party() throws Throwable {
    }

    @When("^I access the Usage Points API$")
    public void I_access_the_Usage_Points_API() throws Throwable {
        driver.get(StepUtils.BASE_URL + "/api/feed");
    }

    @Then("^I should see Usage Points$")
    public void I_should_see_Usage_Points() throws Throwable {
        assertXpathExists("/:feed", driver.getPageSource());
        assertXpathExists("/:feed/:entry", driver.getPageSource());
        assertXpathValue("Front Electric Meter", "/:feed/:entry[1]/:title", driver.getPageSource());
    }
}

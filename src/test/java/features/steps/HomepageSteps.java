package features.steps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import static org.junit.Assert.assertTrue;


public class HomepageSteps {

    private WebDriver driver = new HtmlUnitDriver();

    @Given("^I am Data Custodian$")
    public void I_am_Data_Custodian() throws Throwable {
    }

    @When("^I navigate to the home page$")
    public void I_navigate_to_the_home_page() throws Throwable {
        driver.get("http://localhost:8080/");
    }

    @Then("^I should see \"([^\"]*)\"$")
    public void I_should_see(String text) throws Throwable {
        assertTrue(driver.getPageSource().contains(text));
    }
}

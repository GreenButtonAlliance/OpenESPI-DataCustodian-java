package features.steps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.runtime.PendingException;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.openqa.selenium.WebDriver;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;

import static org.custommonkey.xmlunit.XMLAssert.*;
import static org.energyos.espi.datacustodian.Asserts.assertXpathValue;

public class APISteps {
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
        assertXpathExists("/feed", driver.getPageSource());
        assertXpathExists("/feed/entry", driver.getPageSource());
        assertXpathValue("House meter", "/feed/entry[1]/title", driver.getPageSource());
    }
}

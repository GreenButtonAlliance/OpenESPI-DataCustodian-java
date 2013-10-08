/*
 * Copyright 2013 EnergyOS.org
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package features.steps;

import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.custommonkey.xmlunit.XMLUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static features.steps.StepUtils.*;
import static org.energyos.espi.datacustodian.support.Asserts.assertXpathValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class RetailCustomerSteps {

    private WebDriver driver = WebDriverSingleton.getInstance();
    String xmlResult;

    @Before
    public void setup() {
        XMLUnit.getControlDocumentBuilderFactory().setNamespaceAware(false);
    }

    @Given("^there is an Alan Turing retail customer$")
    public void there_is_an_Alan_Turing_retail_customer() throws Throwable {
    }

    @When("^I navigate to customer list page$")
    public void I_navigate_to_customer_list_page() throws Throwable {
        navigateTo("/custodian/retailcustomers");
    }

    @Then("^I should see Alan Turing in the customer list$")
    public void I_should_see_Alan_Turing_in_the_customer_list() throws Throwable {
        assertTrue(driver.getPageSource().contains("Turing"));
    }

    @Then("^there is a Alan Turing retail customer$")
    public void there_is_a_Alan_Turing_retail_customer() throws Throwable {
    }

    @Then("^I should see Grace Hopper in the customer list$")
    public void I_should_see_Grace_Hopper_in_the_customer_list() throws Throwable {
        assertTrue(driver.getPageSource().contains("Grace"));
        assertTrue(driver.getPageSource().contains("Hopper"));
    }

    @Given("^I have a Retail Customer account$")
    public void I_have_a_Retail_Customer_account() throws Throwable {
    }

    @When("^I log in as Alan Turing$")
    public void I_log_in_as_Alan_Turing() throws Throwable {
        StepUtils.login("alan", "koala");
    }

    @Then("^I should see Retail Customer home page$")
    public void I_should_see_Retail_Customer_home_page() throws Throwable {
        assertTrue(driver.getCurrentUrl().endsWith("/RetailCustomer/1/home"));
        assertTrue(driver.getPageSource().contains("Welcome Retail Customer"));
    }

    @Then("^I should be able to download Usage Points in XML format$")
    public void I_should_be_able_to_download_Usage_Points_in_XML_format() throws Throwable {
        WebElement downloadLink = driver.findElement(By.partialLinkText("Download XML"));
        downloadLink.click();

        xmlResult = driver.getPageSource();

        assertXpathValue("Front Electric Meter", "feed/entry[1]/title", xmlResult);
    }

    @Then("^the logged in retail customer can see their usage data$")
    public void the_logged_in_retail_customer_can_see_their_usage_data() throws Throwable {

        clickLinkByText("Usage Points");
        assertTrue(driver.getPageSource().contains("Front Electric Meter"));

        clickLinkByText("Front Electric Meter");
        SharedSteps.assertUsagePoint();
        SharedSteps.assertUsageSummary();
        SharedSteps.assertQualitySummary();
        SharedSteps.assertLocalTimeParameters();

        clickLinkByText("Fifteen Minute Electricity Consumption");
        SharedSteps.assertMeterReading();
        SharedSteps.assertReadingType();
        SharedSteps.assertIntervalBlocks();
        SharedSteps.assertIntervalReadings();
        SharedSteps.assertReadingQualities();
    }

    @Then("^the XML includes Service categories$")
    public void the_XML_includes_Service_categories() throws Throwable {
        assertXpathValue("0", "feed/entry[1]/content/UsagePoint/ServiceCategory/kind", xmlResult);
    }

    @Then("^the XML includes Meter Readings$")
    public void the_XML_includes_Meter_Readings() throws Throwable {
        assertXpathValue("Fifteen Minute Electricity Consumption", "feed/entry[2]/title", xmlResult);
    }

    @Then("^the XML includes Reading Types$")
    public void the_XML_includes_Reading_Types() throws Throwable {
        assertXpathValue("Energy Delivered (kWh)", "feed/entry[3]/title", xmlResult);
    }

    @Then("^the XML includes Electric Power Usage Summary$")
    public void the_XML_includes_Electric_Power_Usage_Summary() throws Throwable {
        assertXpathValue("Usage Summary", "feed/entry/content/ElectricPowerUsageSummary/../../title", xmlResult);
        assertXpathValue("1119600", "feed/entry[5]/content/ElectricPowerUsageSummary/billingPeriod/duration", xmlResult);
    }

    @Then("^the XML includes Interval Blocks$")
    public void the_XML_includes_Interval_Blocks() throws Throwable {
        assertXpathValue("86400", "feed/entry[4]/content/IntervalBlock/interval/duration", xmlResult);
    }

    @Then("^the XML includes Interval Readings$")
    public void the_XML_includes_Interval_Readings() throws Throwable {
        assertXpathValue("974", "feed/entry[4]/content/IntervalBlock/IntervalReading[1]/cost", xmlResult);
        assertXpathValue("900", "feed/entry[4]/content/IntervalBlock/IntervalReading[1]/timePeriod/duration", xmlResult);
        assertXpathValue("1330578900", "feed/entry[4]/content/IntervalBlock/IntervalReading[2]/timePeriod/start", xmlResult);
        assertXpathValue("965", "feed/entry[4]/content/IntervalBlock/IntervalReading[2]/cost", xmlResult);
    }


    @When("^I visit the home page$")
    public void I_visit_the_home_page() throws Throwable {
        StepUtils.navigateTo("/home");
    }

    @Then("^I should see the option to login$")
    public void I_should_see_the_option_to_login() throws Throwable {
        assertNotNull(driver.findElement(By.id("login")));
    }

    @Given("^a Retail Customer$")
    public void a_Retail_Customer() throws Throwable {
        CucumberSession.setUsername(newUsername());
        StepUtils.registerUser(CucumberSession.getUsername(), newFirstName(), newLastName(), StepUtils.PASSWORD);
    }

    @When("^I log in as Retail Customer$")
    public void I_log_in_as() throws Throwable {
        StepUtils.login(CucumberSession.getUsername(), StepUtils.PASSWORD);
    }

    @Then("^I should see a Select Third Party link$")
    public void I_should_see_a_Select_Third_Party_link() throws Throwable {
        assertNotNull(driver.findElement(By.linkText("Select Authorized Third Party")));
    }

    @When("^I click on the Select Third Party link$")
    public void I_click_on_the_Select_Third_Party_link() throws Throwable {
        clickLinkByText("Select Authorized Third Party");
    }

    @Then("^I should see the Third Party list$")
    public void I_should_see_the_Third_Party_list() throws Throwable {
        String pageSource = driver.getPageSource();

        assertContains("Third Party List", pageSource);
        assertNotNull(driver.findElement(By.name("Third_party")));
    }

    @Given("^There is a Third Party$")
    public void There_is_a_Third_Party() throws Throwable {
       // Defined in seed data
    }

    @When("^I select the Third Party$")
    public void I_select_the_Third_Party() throws Throwable {
        selectRadioByLabel("Pivotal Energy");
        driver.findElement(By.name("next")).click();
    }

    @Then("^I should be taken to the Third Party login page$")
    public void I_should_be_taken_to_the_Third_Party_login_page() throws Throwable {
        assertNotNull("Login field missing", driver.findElement(By.name("j_username")));
        assertNotNull("Password field missing", driver.findElement(By.name("j_password")));
    }

    @And("^the XML includes Reading Qualities$")
    public void the_XML_includes_Reading_Qualities() throws Throwable {
        assertXpathValue("8", "feed/entry[4]/content/IntervalBlock[1]/IntervalReading[1]/ReadingQuality[1]", xmlResult);
    }

    @Then("^the XML includes Electric Power Quality Summary$")
    public void the_XML_includes_Electric_Power_Quality_Summary() throws Throwable {
        assertXpathValue("Quality Summary", "feed/entry/content/ElectricPowerQualitySummary/../../title", xmlResult);
        assertXpathValue("2119600", "feed/entry[6]/content/ElectricPowerQualitySummary/summaryInterval/duration", xmlResult);
    }
}

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
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.custommonkey.xmlunit.XMLUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.energyos.espi.datacustodian.Asserts.assertXpathValue;
import static org.junit.Assert.assertTrue;

public class RetailCustomerSteps {

    private WebDriver driver = WebDriverSingleton.getInstance();
    private String username;

    @Before
    public void setup() {
        XMLUnit.getControlDocumentBuilderFactory().setNamespaceAware(false);
    }

    @Given("^I am a Data Custodian$")
    public void I_am_a_Data_Custodian() throws Throwable {
    }

    @Given("^there is an Alan Turing retail customer$")
    public void there_is_an_Alan_Turing_retail_customer() throws Throwable {
    }

    @When("^I navigate to customer list page$")
    public void I_navigate_to_customer_list_page() throws Throwable {
        driver.get(StepUtils.BASE_URL + "/custodian/retailcustomers");
    }

    @Then("^I should see Alan Turing in the customer list$")
    public void I_should_see_Alan_Turing_in_the_customer_list() throws Throwable {
        assertTrue(driver.getPageSource().contains("Turing"));
    }

    @Then("^there is a Alan Turing retail customer$")
    public void there_is_a_Alan_Turing_retail_customer() throws Throwable {
    }

    @Given("^a logged in retail customer$")
    public void a_logged_in_retail_customer() throws Throwable {
        StepUtils.login("alan", "koala");
    }

    @When("^I look at my usage page$")
    public void I_look_at_my_usage_page() throws Throwable {
        driver.get(StepUtils.BASE_URL + "/customer/usagepoints");
    }

    @Then("^I should see my Usage Points with title \"([^\"]*)\"$")
    public void I_should_see_my_Usage_Points_with_title(String title) throws Throwable {
        assertTrue(driver.getPageSource().contains(title));
    }

    @Then("^I should see my Usage Points with Service Categories with Service Kind of \"ELECTRICITY_SERVICE\"$")
    public void I_should_see_my_Usage_Points_with_title_Electricity_Service() throws Throwable {
        assertTrue(driver.getPageSource().contains("ELECTRICITY_SERVICE"));
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
        assertTrue(driver.getCurrentUrl().endsWith("/customer/home"));
        assertTrue(driver.getPageSource().contains("Welcome Retail Customer"));
    }

    @Then("^I should be able to download Usage Points in XML format$")
    public void I_should_be_able_to_download_Usage_Points_in_XML_format() throws Throwable {
        WebElement downloadLink = driver.findElement(By.partialLinkText("Download XML"));
        downloadLink.click();

        String xmlResult = driver.getPageSource();

        assertXpathValue("House meter", "feed/entry[1]/title", xmlResult);
        assertXpathValue("Gas meter", "feed/entry[2]/title", xmlResult);
    }

    @Given("^Usage Points with Service Categories$")
    public void Usage_Points_with_Service_Categories() throws Throwable {
    }

    @Then("^I should be able to download my Usage Points with Service Categories with Service Kind of Gas service$")
    public void I_should_be_able_to_download_Usage_Points_with_Service_Categories_with_Service_Kind_of_Gas_service() throws Throwable {
        WebElement downloadLink = driver.findElement(By.partialLinkText("Download XML"));
        downloadLink.click();

        assertXpathEvaluatesTo("1", "feed/entry[1]/content/UsagePoint/ServiceCategory/kind", StepUtils.flattenXml(driver.getPageSource()));
    }

    @Then("^I should see Meter Reading$")
    public void I_should_see_Meter_Reading() throws Throwable {
        assertTrue(driver.getPageSource().contains("86400"));
    }

    @Given("^a logged in Retail Customer with Usage Points$")
    public void a_logged_in_Retail_Customer_with_Usage_Points() throws Throwable {

    }
}

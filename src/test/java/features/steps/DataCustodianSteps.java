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

import static features.steps.StepUtils.associate;
import static features.steps.StepUtils.navigateTo;
import static org.energyos.espi.common.test.BaseStepUtils.clickLinkByText;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.energyos.espi.common.test.CucumberSession;
import org.energyos.espi.common.test.WebDriverSingleton;
import org.junit.Ignore;
import org.openqa.selenium.WebDriver;

import cucumber.api.java.After;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class DataCustodianSteps {

    private WebDriver driver = WebDriverSingleton.getInstance();

    @After
    public void logout() {
        navigateTo("/logout.do");
    }

    @Given("^I am a Data Custodian$")
    public void I_am_a_Data_Custodian() throws Throwable {
    }

    @Given("^I have a Data Custodian account$")
    public void I_have_a_Data_Custodian_account() throws Throwable {
    }

    @When("^I log in as a Data Custodian with invalid credentials$")
    public void I_log_in_as_Grace_Hopper_with_invalid_credentials() throws Throwable {
        StepUtils.login("grace", "invalid_password");
    }

    @Then("^I should be logged in$")
    public void I_should_be_logged_in() throws Throwable {
        assertTrue(driver.getPageSource().contains("Logout"));
    }

    @Then("^I should see login form$")
    public void I_should_see_login_form() throws Throwable {
        assertTrue(driver.getPageSource().contains("Sign in"));
    }

    @And("^I am not logged in$")
    public void I_am_not_logged_in() throws Throwable {
    }

    @Then("^I should see Data Custodian home page$")
    public void I_should_see_Data_Custodian_home_page() throws Throwable {
        assertTrue(driver.getCurrentUrl().endsWith("/custodian/home"));
        assertTrue(driver.getPageSource().contains("Welcome to the"));
    }

    @When("^I log in as a Data Custodian$")
    public void I_login_as_a_Data_Custodian() throws Throwable {
        StepUtils.login("grace", StepUtils.PASSWORD);
    }

    @When("^I upload Usage Points")
    @Ignore
    public void I_upload_Usage_Points() throws Throwable {
        StepUtils.uploadUsagePoints(CucumberSession.getUUID());
    }

    @When("^I login as Alan Turing$")
    public void I_login_as_Alan_Turing() throws Throwable {
        StepUtils.login("alan", StepUtils.PASSWORD);
    }

    @And("^I navigate to the Usage Points list$")
    public void I_navigate_to_the_Usage_Points_list() throws Throwable {
        clickLinkByText("Usage Points");
    }

    @And("^I select \"Alan Turing\" from customer list$")
    public void I_select_from_customer_list() throws Throwable {
        clickLinkByText("alan");
    }

    @Then("^I should see \"([^\"]*)\" profile page$")
    public void I_should_see_profile_page(String arg1) throws Throwable {
        assertTrue(driver.getPageSource().contains("Alan Turing"));
    }

    @And("^I create a new Retail Customer$")
    public void I_create_a_new_Retail_Customer() throws Throwable {
        CucumberSession.setUsername(StepUtils.newUsername());
        StepUtils.registerUser(CucumberSession.getUsername(), StepUtils.newFirstName(), StepUtils.newLastName(), StepUtils.PASSWORD);
    }

    @Then("^I should see the new Retail Customer in the customer list$")
    public void I_should_see_the_new_Retail_Customer_in_the_customer_list() throws Throwable {
        assertTrue(driver.getPageSource().contains(CucumberSession.getUsername()));
    }

    @And("^I select Retail Customer from customer list$")
    public void I_select_Retail_Customer_from_customer_list() throws Throwable {
        clickLinkByText(CucumberSession.getUsername());
    }

    @Given("^a Retail Customer with Usage Points$")
    @Ignore
    public void a_Retail_Customer_with_Usage_Points() throws Throwable {
        CucumberSession.setUsername(StepUtils.newUsername());

        StepUtils.registerUser(CucumberSession.getUsername(), StepUtils.newFirstName(), StepUtils.newLastName(), StepUtils.PASSWORD);
        CucumberSession.setUUID(UUID.randomUUID());
        StepUtils.addUsagePoint(CucumberSession.getUsername(), CucumberSession.getUUID().toString());
        StepUtils.importUsagePoint(CucumberSession.getUUID());
        UUID secondUsagePointUUID = UUID.randomUUID();
        StepUtils.addUsagePoint(CucumberSession.getUsername(), secondUsagePointUUID.toString());
        StepUtils.importUsagePoint(secondUsagePointUUID);
        StepUtils.login(CucumberSession.getUsername(), StepUtils.PASSWORD);
        CucumberSession.setUsagePointHashedId(StepUtils.getFirstUsagePointHashedId());
    }

    @And("^I associate \"([^\"]*)\" Usage Point with Retail Customer$")
    @Ignore
    public void I_associate_Usage_Point_with_Retail_Customer(String usagePointDescription) throws Throwable {
        navigateTo("/custodian/retailcustomers");
        clickLinkByText(CucumberSession.getUsername());
        CucumberSession.setUUID(UUID.randomUUID());
        associate(CucumberSession.getUUID().toString(), usagePointDescription);
    }

    @Then("^I should see \"([^\"]*)\"$")
    @Ignore
    public void I_should_see(String content) throws Throwable {
        assertTrue("Page should contain '" + content + "'", driver.getPageSource().contains(content));
    }
}

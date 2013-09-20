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

import cucumber.api.java.After;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openqa.selenium.WebDriver;

import java.util.UUID;

import static features.steps.StepUtils.*;
import static org.junit.Assert.assertTrue;

public class DataCustodianSteps {

    private WebDriver driver = WebDriverSingleton.getInstance();
    private String username;

    @After
    public void logout() {
        navigateTo("/logout.do");
    }

    @Given("^I have a Data Custodian account$")
    public void I_have_a_Data_Custodian_account() throws Throwable {
    }

    @When("^I log in as Grace Hopper$")
    public void I_log_in_as_Grace_Hopper() throws Throwable {
        StepUtils.login("grace", "koala");
    }

    @When("^I log in as Grace Hopper with invalid credentials$")
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

    @And("^I am logged in as Grace Hopper$")
    public void I_am_logged_in_as_Grace_Hopper() throws Throwable {
        StepUtils.login("grace", "koala");
    }

    @Then("^I should see Data Custodian home page$")
    public void I_should_see_Data_Custodian_home_page() throws Throwable {
        assertTrue(driver.getCurrentUrl().endsWith("/custodian/home"));
        assertTrue(driver.getPageSource().contains("Welcome Data Custodian"));
    }

    @Given("^Grace Hopper Data Custodian$")
    public void Grace_Hopper_Data_Custodian() throws Throwable {
    }

    @Given("^Alan Turing Retail Customer$")
    public void Alan_Turing_Retail_Customer() throws Throwable {
    }

    @When("^I login as Grace Hopper$")
    public void I_login_as_Grace_Hopper() throws Throwable {
        StepUtils.login("grace", StepUtils.PASSWORD);
    }

    @When("^I upload Usage Points")
    public void I_upload_Usage_Points() throws Throwable {
        StepUtils.uploadUsagePoints();
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

    @And("^I select a Usage Point$")
    public void I_select_Usage_Point() throws Throwable {
        clickLinkByText("Front Electric Meter");
    }

    @And("^I select Meter Reading$")
    public void I_select_Meter_Reading() throws Throwable {
        clickLinkByText("Fifteen Minute Electricity Consumption");
    }

    @Then("^I should see my Meter Reading with Interval Blocks and Readings$")
    public void I_should_see_my_Meter_Reading_with_Interval_Blocks() throws Throwable {
        assertTrue(driver.getPageSource().contains("1330578000"));
        assertTrue(driver.getPageSource().contains("1330664400"));
        assertTrue(driver.getPageSource().contains("974"));
        assertTrue(driver.getPageSource().contains("900"));
        assertTrue(driver.getPageSource().contains("965"));
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

    @When("^I login as Retail Customer$")
    public void I_login_as_Retail_Customer() throws Throwable {
        StepUtils.login(CucumberSession.getUsername(), StepUtils.PASSWORD);
    }

    @Given("^a Retail Customer with Usage Points$")
    public void a_Retail_Customer_with_Usage_Points() throws Throwable {
        CucumberSession.setUsername(StepUtils.newUsername());

        StepUtils.registerUser(CucumberSession.getUsername(), StepUtils.newFirstName(), StepUtils.newLastName(), StepUtils.PASSWORD);
        CucumberSession.setUUID(UUID.randomUUID());
        StepUtils.addUsagePoint(CucumberSession.getUsername(), CucumberSession.getUUID().toString());
        StepUtils.importUsagePoint();
        StepUtils.login(CucumberSession.getUsername(), StepUtils.PASSWORD);
    }

    @Then("^I should see my Electric Power Usage Summaries$")
    public void I_should_see_my_Electric_Power_Usage_Summaries() throws Throwable {
        assertTrue(driver.getPageSource().contains("Usage Summary"));
}

    @And("^I navigate to the upload form$")
    public void I_navigate_to_the_upload_form() throws Throwable {
        clickLinkByText("Upload data");
    }

    @And("^I associate \"([^\"]*)\" Usage Point with Retail Customer$")
    public void I_associate_Usage_Point_with_Retail_Customer(String usagePointDescription) throws Throwable {
        navigateTo("/custodian/retailcustomers");
        clickLinkByText(CucumberSession.getUsername());
        CucumberSession.setUUID(UUID.randomUUID());
        associate(CucumberSession.getUUID().toString(), usagePointDescription);
    }

    @Then("^I should see \"([^\"]*)\"$")
    public void I_should_see(String content) throws Throwable {
        assertTrue("Page should contain '" + content + "'", driver.getPageSource().contains(content));
    }
}

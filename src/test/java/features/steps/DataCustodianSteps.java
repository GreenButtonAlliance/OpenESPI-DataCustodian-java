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
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class DataCustodianSteps {

    private WebDriver driver = WebDriverSingleton.getInstance();

    @After
    public void logout() {
        driver.get(StepUtils.BASE_URL + "/j_spring_security_logout");
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
        StepUtils.login("grace", "koala");
    }

    @When("^I upload Usage Points")
    public void I_upload_Usage_Points() throws Throwable {
        WebElement uploadLink = driver.findElement(By.linkText("Upload data"));
        uploadLink.click();
        File cwd = new File(".");
        WebElement file = driver.findElement(By.name("file"));
        file.sendKeys(cwd.getAbsolutePath() + "/etc/usage_point.xml");
        WebElement upload = driver.findElement(By.name("upload"));
        upload.click();
        driver.get("http://localhost:8080/DataCustodian/j_spring_security_logout");
    }

    @When("^I login as Alan Turing$")
    public void I_login_as_Alan_Turing() throws Throwable {
        StepUtils.login("alan", "koala");
    }

    @And("^I navigate to the Usage Points list$")
    public void I_navigate_to_the_Usage_Points_list() throws Throwable {
        WebElement usagePointsLink = driver.findElement(By.linkText("Usage Points"));
        usagePointsLink.click();
    }

    @And("^I select \"Alan Turing\" from customer list$")
    public void I_select_from_customer_list() throws Throwable {
        WebElement customerLink = driver.findElement(By.linkText("Turing"));
        customerLink.click();
    }

    @Then("^I should see \"([^\"]*)\" profile page$")
    public void I_should_see_profile_page(String arg1) throws Throwable {
        assertTrue(driver.getPageSource().contains("Alan Turing"));
    }
}

/*
 * Copyright 2013 EnergyOS ESPI
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

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.assertTrue;

public class RetailCustomerSteps {

    private WebDriver driver = WebDriverSingleton.getInstance();

    @Given("^I am a Data Custodian$")
    public void I_am_a_Data_Custodian() throws Throwable {
    }

    @Given("^there is an Alan Turing retail customer$")
    public void there_is_an_Alan_Turing_retail_customer() throws Throwable {
    }

    @When("^I navigate to customer list page$")
    public void I_navigate_to_customer_list_page() throws Throwable {
        driver.get("http://localhost:8080/retailcustomers");
    }

    @When("^I create a new retail customer with the name Grace Hopper$")
    public void I_create_a_new_retail_customer_with_the_name_Grace_Hopper() throws Throwable {
        driver.get("http://localhost:8080/retailcustomers/form");
        assertTrue(driver.getPageSource().contains("New Retail Customer"));

        WebElement form = driver.findElement(By.name("new_customer"));

        WebElement firstName = form.findElement(By.name("firstName"));
        firstName.sendKeys("Grace");

        WebElement lastName = form.findElement(By.name("lastName"));
        lastName.sendKeys("Hopper");

        WebElement create = form.findElement(By.name("create"));
        create.click();
    }

    @Then("^I should see Alan Turing in the customer list$")
    public void I_should_see_Alan_Turing_in_the_customer_list() throws Throwable {
        assertTrue(driver.getPageSource().contains("Alan"));
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
}

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

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openqa.selenium.WebDriver;

import static org.junit.Assert.assertTrue;

public class UsagePointSteps {

    private WebDriver driver = WebDriverSingleton.getInstance();

    @Given("^I am a Third Party$")
    public void I_am_a_Third_Party() throws Throwable {
    }

    @Given("^there exists a user that has Usage Points$")
    public void there_exists_a_user_that_has_Usage_Points() throws Throwable {
    }

    @When("^I request the usage points for a user$")
    public void I_request_the_usage_points_for_a_user() throws Throwable {
        driver.get("http://localhost:8080/RetailCustomer/1/UsagePoint");
    }

    @Then("^I should receive an xml response with the user's usage points$")
    public void I_should_receive_an_xml_response_with_the_user_s_usage_points() throws Throwable {
        assertTrue(driver.getPageSource().contains("xml"));
        assertTrue(driver.getPageSource().contains("House meter"));
        assertTrue(driver.getPageSource().contains("Gas meter"));
    }
}

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ImportUsagePointSteps {

    private WebDriver driver = WebDriverSingleton.getInstance();
    private int importExitValue;

    @Given("^an XML file$")
    public void an_XML_file() throws Throwable {
    }

    @When("^Data Custodian imports the XML file$")
    public void Data_Custodian_imports_the_XML_file() throws Throwable {
        Runtime r = Runtime.getRuntime();
        Process p = r.exec("bin/import_usage_point.sh etc/usage_point.xml " + StepUtils.BASE_URL + "/custodian/retailcustomers/1/upload");
        p.waitFor();
        importExitValue = p.exitValue();
    }

    @Then("^the import tool should indicate success$")
    public void the_import_tool_should_indicate_success() throws Throwable {
        assertEquals(0, importExitValue);
    }

    @Then("^I should see the imported data$")
    public void I_should_see_the_imported_data() throws Throwable {
        assertTrue(driver.getPageSource().contains("Electric meter"));
    }
}

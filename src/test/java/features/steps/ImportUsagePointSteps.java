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
import org.energyos.espi.datacustodian.console.ImportUsagePoint;
import org.openqa.selenium.WebDriver;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
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
        File tmpFile = File.createTempFile("usage_point", ".xml");
        ClassPathResource sourceFile = new ClassPathResource("/fixtures/usage_point.xml");
        Files.copy(sourceFile.getInputStream(), Paths.get(tmpFile.getAbsolutePath()), REPLACE_EXISTING);

        ImportUsagePoint.main(new String[] {tmpFile.getAbsolutePath(), StepUtils.BASE_URL + "/custodian/retailcustomers/1/upload"});
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

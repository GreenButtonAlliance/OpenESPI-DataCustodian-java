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

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.energyos.espi.datacustodian.console.ImportUsagePoint;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static features.steps.StepUtils.login;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.junit.Assert.assertTrue;

public class ImportUsagePointSteps {

    private WebDriver driver = WebDriverSingleton.getInstance();

    @When("^I import Alan Turing's Usage Points from an XML file$")
    public void Data_Custodian_imports_the_XML_file() throws Throwable {
        File tmpFile = File.createTempFile("usage_point", ".xml");
        ClassPathResource sourceFile = new ClassPathResource("/fixtures/15minLP_15Days.xml");
        Files.copy(sourceFile.getInputStream(), Paths.get(tmpFile.getAbsolutePath()), REPLACE_EXISTING);

        ImportUsagePoint.main(new String[]{tmpFile.getAbsolutePath(), StepUtils.BASE_URL + "/custodian/retailcustomers/1/upload"});
    }

    @Then("^Alan Turing should be able to see the imported data$")
    public void I_should_see_the_imported_data() throws Throwable {
        login("alan", "koala");
        driver.findElement(By.linkText("Usage Points")).click();
        driver.findElement(By.linkText("Front Electric Meter")).click();
        driver.findElement(By.linkText("Fifteen Minute Electricity Consumption")).click();
        assertTrue(driver.getPageSource().contains("86400"));
        assertTrue(driver.getPageSource().contains("1331697600"));
    }
}

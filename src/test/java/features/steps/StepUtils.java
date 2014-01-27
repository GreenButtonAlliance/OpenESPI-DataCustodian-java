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

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.energyos.espi.common.test.BaseStepUtils;
import org.energyos.espi.common.test.CucumberSession;
import org.energyos.espi.common.test.FixtureFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class StepUtils extends BaseStepUtils {
    public static void navigateTo(String path) {
        driver.get(StepUtils.DATA_CUSTODIAN_BASE_URL + path);
    }

    public static void login(String username, String password) {
        navigateTo("/logout.do");
        navigateTo("/");
        WebElement loginLink = driver.findElement(By.id("login"));
        loginLink.click();
        WebElement usernameInput = driver.findElement(By.name("j_username"));
        usernameInput.clear();
        usernameInput.sendKeys(username);
        WebElement passwordInput = driver.findElement(By.name("j_password"));
        passwordInput.clear();
        passwordInput.sendKeys(password);
        WebElement login = driver.findElement(By.name("submit"));

        login.click();
    }

    public static String flattenXml(String xml) {
        return xml.replace("\n", "").replaceAll("\\s+<", "<").replaceAll(">\\s+", ">");
    }

    public static void importUsagePoint(UUID uuid) throws IOException {
        navigateTo("/custodian/upload");
        uploadUsagePoints(uuid);
    }

    public static void addUsagePoint(String username, String mrid) throws IOException {
        navigateTo("/custodian/retailcustomers");
        clickLinkByText(username);
        associate(mrid, "Front Electric Meter");
    }

    public static void registerUser(String username, String firstName, String lastName, String password) {
        StepUtils.login("grace", StepUtils.PASSWORD);

        clickLinkByText("Customer List");
        clickLinkByPartialText("Add new customer");

        assertTrue(driver.getPageSource().contains("New Retail Customer"));

        WebElement form = driver.findElement(By.name("new_customer"));

        WebElement usernameField = form.findElement(By.name("username"));
        usernameField.sendKeys(username);

        WebElement firstNameField = form.findElement(By.name("firstName"));
        firstNameField.sendKeys(firstName);

        WebElement lastNameField = form.findElement(By.name("lastName"));
        lastNameField.sendKeys(lastName);

        WebElement passwordField = form.findElement(By.name("password"));
        passwordField.sendKeys(password);

        WebElement create = form.findElement(By.name("create"));
        create.click();

        assertTrue(driver.getPageSource().contains("Retail Customers"));

        WebElement retailCustomerLink = driver.findElement(By.linkText(username));
        String href = retailCustomerLink.getAttribute("href");
        Pattern pattern = Pattern.compile("retailcustomers/(\\d+)");
        Matcher matcher = pattern.matcher(href);
        matcher.find();
        String hashedId = matcher.group(1);
        assertNotNull(hashedId);
        CucumberSession.setUserHashedId(hashedId);
    }

    public static void associate(String uuid, String description) {
        clickLinkByPartialText("Add Usage Point");

        WebElement uuidElement = driver.findElement(By.name("UUID"));
        uuidElement.clear();
        uuidElement.sendKeys(uuid);

        WebElement descriptionElement = driver.findElement(By.id("description"));
        descriptionElement.sendKeys(description);

        WebElement create = driver.findElement(By.name("create"));
        create.click();
    }

    public static void uploadUsagePoints(UUID uuid) throws IOException {
        String xml = FixtureFactory.newFeedXML(uuid);
        File tmpFile = File.createTempFile("usage_point", ".xml");
        Files.copy(new ByteArrayInputStream(xml.getBytes()), Paths.get(tmpFile.getAbsolutePath()), REPLACE_EXISTING);

        clickLinkByText("Upload");
        WebElement file = driver.findElement(By.name("file"));
        file.sendKeys(tmpFile.getAbsolutePath());
        WebElement upload = driver.findElement(By.name("upload"));
        upload.click();
    }

    public static void assertContains(String s, String pageSource) {
        assertTrue("Missing content '" + s + "'", pageSource.contains(s));
    }

    public static String getFirstUsagePointHashedId() {
        clickLinkByText("Usage Points");

        WebElement usagePointLink = driver.findElement(By.className("usage-point"));
        String href = usagePointLink.getAttribute("href");
        Pattern pattern = Pattern.compile("UsagePoint/(.+)/show");
        Matcher matcher = pattern.matcher(href);
        matcher.find();
        String hashedId = matcher.group(1);
        assertNotNull(hashedId);

        return hashedId;
    }



}

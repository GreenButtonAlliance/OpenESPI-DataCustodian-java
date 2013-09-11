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

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static junit.framework.Assert.assertTrue;

public class StepUtils {

    public final static String BASE_URL = "http://localhost:8080/DataCustodian";
    private static WebDriver driver = WebDriverSingleton.getInstance();;

    public static void navigateTo(String path) {
        driver.get(StepUtils.BASE_URL + path);
    }

    public static void clickLinkByText(String linkText) {
        WebElement link = driver.findElement(By.linkText(linkText));
        link.click();
    }

    public static void login(String username, String password) {
        navigateTo("/j_spring_security_logout");
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

    public static void importUsagePoint(String username, String path) throws IOException {
        clickLinkByText(username);

        uploadUsagePoints(path);
    }

    public static void registerUser(String username, String firstName, String lastName, String password) {
        StepUtils.login("grace", "koala");

        clickLinkByText("Customer List");
        clickLinkByText("Add new customer");

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

        assertTrue(driver.getPageSource().contains("Retail customer created"));
    }

    public static String newLastName() {
        return "Doe" + System.currentTimeMillis();
    }

    public static String newFirstName() {
        return "John" + System.currentTimeMillis();
    }

    public static String newUsername() {
        return "User" + System.currentTimeMillis();
    }

    public static void uploadUsagePoints(String path) throws IOException {
        File tmpFile = File.createTempFile("usage_point", ".xml");
        ClassPathResource sourceFile = new ClassPathResource(path);
        Files.copy(sourceFile.getInputStream(), Paths.get(tmpFile.getAbsolutePath()), REPLACE_EXISTING);

        clickLinkByText("Upload data");
        WebElement file = driver.findElement(By.name("file"));
        file.sendKeys(tmpFile.getAbsolutePath());
        WebElement upload = driver.findElement(By.name("upload"));
        upload.click();
    }
}

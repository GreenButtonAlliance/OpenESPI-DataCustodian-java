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

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.energyos.espi.common.test.WebDriverSingleton;
import org.junit.Ignore;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SharedSteps {

    private static WebDriver driver = WebDriverSingleton.getInstance();

    @Ignore
    static void assertMeterReading() {
        assertTrue(driver.getPageSource().contains("Meter Reading: Fifteen Minute Electricity Consumption"));
    }
    @Ignore
    static void assertReadingType() {
        assertTrue(driver.getPageSource().contains("Type of Meter Reading Data"));
        assertTrue(driver.getPageSource().contains("840"));
        assertTrue(driver.getPageSource().contains("12"));
        assertTrue(driver.getPageSource().contains("1/2"));
        assertTrue(driver.getPageSource().contains("600/800"));
    }
    @Ignore
    static void assertIntervalReadings() {
        assertTrue(driver.getPageSource().contains("974"));
        assertTrue(driver.getPageSource().contains("900"));
        assertTrue(driver.getPageSource().contains("965"));
    }
    @Ignore
    static void assertIntervalBlocks() {
        assertTrue(driver.getPageSource().contains("86400"));
        assertTrue(driver.getPageSource().contains("1330578000"));
        assertTrue(driver.getPageSource().contains("1330664400"));
    }
    @Ignore
    static void assertUsagePoint() {
        assertTrue(driver.getPageSource().contains("Usage Point: Front Electric Meter"));
        assertTrue(driver.getPageSource().contains("ELECTRICITY_SERVICE"));
    }
    @Ignore
    static void assertUsageSummary() {
        assertTrue(driver.getPageSource().contains("Usage Summary"));
        assertTrue(driver.getPageSource().contains("1119600"));
    }
    @Ignore
    static void assertReadingQualities() {
        WebElement element = driver.findElement(By.cssSelector(".reading-qualities"));

        assertThat(element.getText(), containsString("8"));
    }
    @Ignore
    public static void assertLocalTimeParameters() {
        assertTrue(driver.getPageSource().contains("Local time zone offset from UTCTime"));
        assertTrue(driver.getPageSource().contains("-18000"));
    }
    @Ignore
    public static void assertQualitySummary() {
        assertTrue(driver.getPageSource().contains("Quality Summary"));
        assertTrue(driver.getPageSource().contains("2119600"));
    }
}

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

import org.openqa.selenium.WebDriver;

import static org.junit.Assert.assertTrue;

public class SharedSteps {

    private static WebDriver driver = WebDriverSingleton.getInstance();

    static void assertMeterReading() {
        assertTrue(driver.getPageSource().contains("Meter Reading: Fifteen Minute Electricity Consumption"));
    }

    static void assertReadingType() {
        assertTrue(driver.getPageSource().contains("Energy Delivered (kWh)"));
        assertTrue(driver.getPageSource().contains("840"));
        assertTrue(driver.getPageSource().contains("12"));
        assertTrue(driver.getPageSource().contains("1/2"));
        assertTrue(driver.getPageSource().contains("600/800"));
    }

    static void assertIntervalReadings() {
        assertTrue(driver.getPageSource().contains("974"));
        assertTrue(driver.getPageSource().contains("900"));
        assertTrue(driver.getPageSource().contains("965"));
    }

    static void assertIntervalBlocks() {
        assertTrue(driver.getPageSource().contains("86400"));
        assertTrue(driver.getPageSource().contains("1330578000"));
        assertTrue(driver.getPageSource().contains("1330664400"));
    }

    static void assertUsagePoint() {
        assertTrue(driver.getPageSource().contains("Usage Point: Front Electric Meter"));
        assertTrue(driver.getPageSource().contains("ELECTRICITY_SERVICE"));
    }

    static void assertUsageSummary() {
        assertTrue(driver.getPageSource().contains("Usage Summary"));
        assertTrue(driver.getPageSource().contains("1119600"));
    }

    static void assertReadingQualities() {
        assertTrue(driver.getPageSource().contains("quality1"));
    }
}

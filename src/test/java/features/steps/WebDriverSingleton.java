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

import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class WebDriverSingleton {
    private WebDriverSingleton() { }

    private static class SingletonHolder {
        public static final HtmlUnitDriver INSTANCE = new HtmlUnitDriver(BrowserVersion.FIREFOX_17);
    }

    public static WebDriver getInstance() {
        SingletonHolder.INSTANCE.setJavascriptEnabled(true);
        return SingletonHolder.INSTANCE;
    }
}
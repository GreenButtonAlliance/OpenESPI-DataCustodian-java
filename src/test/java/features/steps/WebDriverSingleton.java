package features.steps;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class WebDriverSingleton {
    private WebDriverSingleton() { }

    private static class SingletonHolder {
        public static final WebDriver INSTANCE = new HtmlUnitDriver();
    }

    public static WebDriver getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
package features.steps;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class StepUtils {

    public static void login(String username, String password) {
        WebDriver driver = WebDriverSingleton.getInstance();
        driver.get("http://localhost:8080/");
        WebElement loginLink = driver.findElement(By.id("login"));
        loginLink.click();
        WebElement usernameInput = driver.findElement(By.name("j_username"));
        usernameInput.sendKeys(username);
        WebElement passwordInput = driver.findElement(By.name("j_password"));
        passwordInput.sendKeys(password);
        WebElement login = driver.findElement(By.name("submit"));
        login.click();
    }
}

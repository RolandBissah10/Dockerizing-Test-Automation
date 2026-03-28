package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object for the Swag Labs Login page.
 * Encapsulates all login-related interactions.
 */
public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Locators
    private final By usernameField    = By.id("user-name");
    private final By passwordField    = By.id("password");
    private final By loginButton      = By.id("login-button");
    private final By errorMessage     = By.cssSelector("[data-test='error']");
    private final By inventoryTitle   = By.cssSelector(".title");

    public static final String URL      = "https://www.saucedemo.com";
    public static final String VALID_USER = "standard_user";
    public static final String VALID_PASS = "secret_sauce";
    public static final String LOCKED_USER = "locked_out_user";

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void open() {
        driver.get(URL);
    }

    public void enterUsername(String username) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(usernameField));
        field.clear();
        field.sendKeys(username);
    }

    public void enterPassword(String password) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordField));
        field.clear();
        field.sendKeys(password);
    }

    public void clickLogin() {
        driver.findElement(loginButton).click();
    }

    public void loginAs(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }

    public String getErrorMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage)).getText();
    }

    public boolean isInventoryPageDisplayed() {
        try {
            WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(inventoryTitle));
            return title.getText().equalsIgnoreCase("Products");
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isOnLoginPage() {
        return driver.getCurrentUrl().equals(URL + "/") || driver.getCurrentUrl().equals(URL);
    }
}

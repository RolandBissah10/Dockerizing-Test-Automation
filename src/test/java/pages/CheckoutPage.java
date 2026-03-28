package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object for the Swag Labs Checkout flow.
 * Covers Step 1 (customer info), Step 2 (order overview), and the confirmation page.
 */
public class CheckoutPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final JavascriptExecutor js;

    // Step 1 - Customer info form
    private final By firstNameField  = By.id("first-name");
    private final By lastNameField   = By.id("last-name");
    private final By postalCodeField = By.id("postal-code");
    private final By continueButton  = By.id("continue");
    private final By errorMessage    = By.cssSelector("[data-test='error']");

    // Step 2 - Order overview
    private final By finishButton    = By.id("finish");
    private final By itemTotal       = By.cssSelector(".summary_subtotal_label");
    private final By taxLabel        = By.cssSelector(".summary_tax_label");
    private final By totalLabel      = By.cssSelector(".summary_total_label");

    // Confirmation page
    private final By confirmHeader   = By.cssSelector(".complete-header");
    private final By backHomeButton  = By.id("back-to-products");

    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(15));
        this.js     = (JavascriptExecutor) driver;
    }

    /**
     * Clicks an element via JavaScript.
     * Bypasses CSS overlays or animations that block normal Selenium clicks in headless CI.
     */
    private void jsClick(WebElement element) {
        js.executeScript("arguments[0].scrollIntoView(true);", element);
        js.executeScript("arguments[0].click();", element);
    }

    // ── Step 1: Customer Information ─────────────────────────────────────────

    /**
     * Fills in the customer info form fields.
     * Waits for each field to be visible before interacting — ensures the
     * Step 1 page is fully rendered before typing.
     */
    public void fillCustomerInfo(String firstName, String lastName, String postalCode) {
        WebElement fn = wait.until(ExpectedConditions.visibilityOfElementLocated(firstNameField));
        fn.clear();
        fn.sendKeys(firstName);

        WebElement ln = wait.until(ExpectedConditions.visibilityOfElementLocated(lastNameField));
        ln.clear();
        ln.sendKeys(lastName);

        WebElement pc = wait.until(ExpectedConditions.visibilityOfElementLocated(postalCodeField));
        pc.clear();
        pc.sendKeys(postalCode);
    }

    /**
     * Clicks the Continue button via JavaScript.
     *
     * FIX: Normal Selenium click was being silently blocked by headless Chrome
     * in CI — same overlay/animation issue as CartPage. JS click bypasses this.
     *
     * No URL wait here because:
     * - Valid form → URL changes to step-two (handled by isOnStep2())
     * - Invalid form → URL stays on step-one, error appears (handled by getErrorMessage())
     */
    public void clickContinue() {
        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(continueButton));
        jsClick(btn);
    }

    /**
     * Returns the validation error shown on Step 1.
     * Waits for the error element to become visible after Continue is clicked.
     */
    public String getErrorMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage)).getText();
    }

    public boolean isOnStep1() {
        return driver.getCurrentUrl().contains("/checkout-step-one.html");
    }

    // ── Step 2: Order Overview ────────────────────────────────────────────────

    /**
     * Checks if the browser is on Step 2.
     * Actively waits for the URL to change rather than just reading it instantly.
     */
    public boolean isOnStep2() {
        try {
            wait.until(ExpectedConditions.urlContains("/checkout-step-two.html"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getItemTotal() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(itemTotal)).getText();
    }

    public String getTax() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(taxLabel)).getText();
    }

    public String getOrderTotal() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(totalLabel)).getText();
    }

    /**
     * Clicks Finish via JavaScript and waits for the confirmation page URL.
     *
     * FIX: Same headless CI click-blocking issue as Continue button.
     * JS click ensures the event fires regardless of overlay state.
     */
    public void clickFinish() {
        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(finishButton));
        jsClick(btn);
        wait.until(ExpectedConditions.urlContains("/checkout-complete.html"));
    }

    // ── Confirmation Page ─────────────────────────────────────────────────────

    public boolean isOnConfirmationPage() {
        return driver.getCurrentUrl().contains("/checkout-complete.html");
    }

    public String getConfirmationHeader() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(confirmHeader)).getText();
    }

    public void clickBackHome() {
        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(backHomeButton));
        jsClick(btn);
        wait.until(ExpectedConditions.urlContains("/inventory.html"));
    }
}
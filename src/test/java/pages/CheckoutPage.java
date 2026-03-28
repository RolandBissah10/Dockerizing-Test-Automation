package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
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
     * Clicks an element via JavaScript — bypasses overlay/animation issues
     * that block normal Selenium clicks in headless CI.
     */
    private void jsClick(WebElement element) {
        js.executeScript("arguments[0].scrollIntoView(true);", element);
        js.executeScript("arguments[0].click();", element);
    }

    /**
     * Clears and fills a form field reliably in headless Chrome.
     *
     * FIX: React-controlled inputs ignore Selenium's clear() in headless CI —
     * the field appears cleared but React's internal state still holds the old value,
     * so validation fires against the stale state. The fix:
     * 1. Click the field to focus it
     * 2. Select all existing text with Ctrl+A
     * 3. Type the new value (or send empty string to leave blank)
     * This triggers React's onChange correctly in all environments.
     */
    private void fillField(By locator, String value) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        field.click();
        field.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        if (value != null && !value.isEmpty()) {
            field.sendKeys(value);
        } else {
            field.sendKeys(Keys.DELETE);
        }
    }

    // ── Step 1: Customer Information ─────────────────────────────────────────

    public void fillCustomerInfo(String firstName, String lastName, String postalCode) {
        fillField(firstNameField, firstName);
        fillField(lastNameField, lastName);
        fillField(postalCodeField, postalCode);
    }

    /**
     * Clicks Continue via JavaScript.
     * No URL wait here — valid form navigates to step-two (isOnStep2 handles that),
     * invalid form stays on step-one and shows an error (getErrorMessage handles that).
     */
    public void clickContinue() {
        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(continueButton));
        jsClick(btn);
    }

    public String getErrorMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage)).getText();
    }

    public boolean isOnStep1() {
        return driver.getCurrentUrl().contains("/checkout-step-one.html");
    }

    // ── Step 2: Order Overview ────────────────────────────────────────────────

    /**
     * Actively waits for the Step 2 URL rather than just reading it instantly.
     * Without this, the check races ahead of navigation in CI and returns false.
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
     * Clicks Finish via JavaScript and waits for the confirmation URL.
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
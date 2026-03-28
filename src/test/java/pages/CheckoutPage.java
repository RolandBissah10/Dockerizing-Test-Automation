package pages;

import org.openqa.selenium.By;
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
    }

    // ── Step 1: Customer Information ─────────────────────────────────────────

    /**
     * Fills in the customer info form fields.
     *
     * FIX: Explicitly waits for each field to be visible before interacting.
     * This ensures the Step 1 page is fully rendered before we try to type,
     * which was causing silent failures in CI where the page loaded slowly.
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
     * Clicks the Continue button.
     *
     * NOTE: No URL wait here — if fields are invalid the URL does NOT change
     * (we stay on Step 1 and an error appears). Each caller handles its own
     * outcome via isOnStep2() or getErrorMessage().
     */
    public void clickContinue() {
        wait.until(ExpectedConditions.elementToBeClickable(continueButton)).click();
    }

    /**
     * Returns the validation error shown on Step 1.
     *
     * FIX: Waits for the error element to become visible — it appears after
     * a short delay following the Continue click, which CI wasn't waiting for.
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
     *
     * FIX: Actively waits for the URL to change rather than just reading it
     * instantly. Without this, the check runs before navigation completes
     * in CI and returns false even when the page is loading correctly.
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
     * Clicks Finish and waits for the confirmation page URL.
     *
     * FIX: Step 2 sometimes renders slowly in CI — the finish button was
     * not yet clickable when the test tried to click it. Now we wait for
     * it to be clickable, click, then confirm navigation via URL wait.
     */
    public void clickFinish() {
        wait.until(ExpectedConditions.elementToBeClickable(finishButton)).click();
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
        wait.until(ExpectedConditions.elementToBeClickable(backHomeButton)).click();
        wait.until(ExpectedConditions.urlContains("/inventory.html"));
    }
}
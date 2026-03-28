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
     * FIX: Using clear() before sendKeys() was unreliable in CI — if the field
     * already has a value, clear() sometimes doesn't trigger the React onChange
     * event, leaving the field in a broken state. We now use an explicit wait
     * for each field to be visible before interacting with it.
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
     * If all fields are valid → navigates to Step 2 (URL changes).
     * If fields are invalid → stays on Step 1 and shows an error message.
     *
     * FIX: We do NOT add a URL wait here because when validation fails,
     * the URL does NOT change — we stay on Step 1. Instead, each individual
     * method (isOnStep2, getErrorMessage) does its own appropriate wait.
     */
    public void clickContinue() {
        wait.until(ExpectedConditions.elementToBeClickable(continueButton)).click();
    }

    /**
     * Returns the validation error message shown on Step 1.
     *
     * FIX: Previously timed out in CI because the error element takes a moment
     * to appear after clicking Continue with empty fields. Explicit visibility
     * wait ensures we give it enough time.
     */
    public String getErrorMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage)).getText();
    }

    public boolean isOnStep1() {
        return driver.getCurrentUrl().contains("/checkout-step-one.html");
    }

    // ── Step 2: Order Overview ────────────────────────────────────────────────

    /**
     * Checks if the browser is currently on Step 2.
     *
     * FIX: Wait for the URL to actually change to step-two before returning.
     * Without this, the URL check runs before navigation completes in CI,
     * returning false even though the page is loading correctly.
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
     * Clicks the Finish button and waits for the confirmation page to load.
     *
     * FIX: Added a wait for the finish button to be clickable AND a URL wait
     * after clicking. In CI, the Step 2 page sometimes renders slowly, making
     * the finish button not yet interactable when clickFinish() is called.
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
package com.swaglabs.pages;

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
    private final By finishButton = By.id("finish");
    private final By itemTotal    = By.cssSelector(".summary_subtotal_label");
    private final By taxLabel     = By.cssSelector(".summary_tax_label");
    private final By totalLabel   = By.cssSelector(".summary_total_label");

    // Confirmation page
    private final By confirmHeader  = By.cssSelector(".complete-header");
    private final By backHomeButton = By.id("back-to-products");

    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(15));
        this.js     = (JavascriptExecutor) driver;
    }

    private void jsClick(WebElement element) {
        js.executeScript("arguments[0].scrollIntoView(true);", element);
        js.executeScript("arguments[0].click();", element);
    }

    /**
     * Sets a React-controlled input field value via JavaScript.
     *
     * WHY THIS IS NEEDED:
     * Swag Labs uses React. React tracks input state internally using a
     * synthetic event system. Selenium's sendKeys() fires native browser
     * events, but in headless Chrome on Linux these events don't always
     * propagate correctly through React's event listeners — the field
     * appears filled visually but React's internal state remains empty,
     * so validation still fails as if the field is blank.
     *
     * THE FIX:
     * We set the value directly on the DOM element's native value setter
     * (bypassing React's override), then manually dispatch an 'input' event.
     * This tricks React into believing the user typed into the field,
     * updating its internal state correctly.
     */
    private void setReactInputValue(By locator, String value) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        js.executeScript(
                "var nativeInputValueSetter = Object.getOwnPropertyDescriptor(" +
                        "    window.HTMLInputElement.prototype, 'value').set;" +
                        "nativeInputValueSetter.call(arguments[0], arguments[1]);" +
                        "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));",
                field, value
        );
    }

    // ── Step 1: Customer Information ─────────────────────────────────────────

    public void fillCustomerInfo(String firstName, String lastName, String postalCode) {
        setReactInputValue(firstNameField,  firstName);
        setReactInputValue(lastNameField,   lastName);
        setReactInputValue(postalCodeField, postalCode);
    }

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

package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object for the Swag Labs Inventory and Cart pages.
 * Handles adding items, viewing the cart, removing items, and checkout navigation.
 */
public class CartPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Inventory page locators
    private final By addToCartButtons    = By.cssSelector("[data-test^='add-to-cart']");
    private final By cartBadge           = By.cssSelector(".shopping_cart_badge");
    private final By cartLink            = By.cssSelector(".shopping_cart_link");

    // Cart page locators
    private final By cartItems           = By.cssSelector(".cart_item");
    private final By removeButtons       = By.cssSelector("[data-test^='remove']");
    private final By checkoutButton      = By.id("checkout");
    private final By continueShoppingBtn = By.id("continue-shopping");

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    /**
     * Adds the first N products to the cart.
     *
     * FIX: Re-fetches buttons on every iteration.
     * After clicking "Add to cart", the button turns into "Remove" and the DOM
     * updates — holding onto the old list causes StaleElementReferenceException,
     * which crashes the session or gives a wrong badge count in CI.
     */
    public void addItemsToCart(int count) {
        for (int i = 0; i < count; i++) {
            List<WebElement> buttons = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(addToCartButtons)
            );
            buttons.get(i).click();
        }
    }

    public boolean isCartBadgeVisible() {
        try {
            return driver.findElement(cartBadge).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getCartBadgeCount() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(cartBadge)).getText();
    }

    /**
     * Clicks the cart icon and waits for the cart page URL to load.
     *
     * FIX: Added URL wait — without it, isOnCartPage() runs before the browser
     * finishes navigating and returns false in CI (slower than local).
     */
    public void openCart() {
        driver.findElement(cartLink).click();
        wait.until(ExpectedConditions.urlContains("/cart.html"));
    }

    public boolean isOnCartPage() {
        return driver.getCurrentUrl().contains("/cart.html");
    }

    public int getCartItemCount() {
        return driver.findElements(cartItems).size();
    }

    /**
     * Removes the first item in the cart and waits for the DOM to update.
     *
     * FIX: Waits for the button to go stale after clicking remove.
     * Without this, getCartItemCount() reads the DOM before it updates
     * and returns the old count (2 instead of 1).
     */
    public void removeFirstItem() {
        List<WebElement> buttons = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(removeButtons)
        );
        WebElement firstRemoveBtn = buttons.get(0);
        firstRemoveBtn.click();
        wait.until(ExpectedConditions.stalenessOf(firstRemoveBtn));
    }

    /**
     * Clicks the Checkout button and waits for Step 1 URL to confirm navigation.
     *
     * FIX: The checkout button requires the cart page to be fully loaded first.
     * We wait for the cart URL before clicking, then wait for step-one URL after.
     * Without these waits, CI fails with "element not clickable: By.id: checkout"
     * because the page hadn't finished loading when the click was attempted.
     */
    public void proceedToCheckout() {
        wait.until(ExpectedConditions.urlContains("/cart.html"));
        wait.until(ExpectedConditions.elementToBeClickable(checkoutButton)).click();
        wait.until(ExpectedConditions.urlContains("/checkout-step-one.html"));
    }

    /**
     * Clicks Continue Shopping and waits for the inventory page to load.
     *
     * FIX: Added URL wait — same timing issue as openCart().
     * CI is slower than local, so the URL check in the test was running
     * before the browser finished navigating back to inventory.
     */
    public void continueShopping() {
        wait.until(ExpectedConditions.elementToBeClickable(continueShoppingBtn)).click();
        wait.until(ExpectedConditions.urlContains("/inventory.html"));
    }
}
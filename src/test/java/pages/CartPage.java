package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object for the Swag Labs Inventory and Cart pages.
 */
public class CartPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final JavascriptExecutor js;

    private final By addToCartButtons    = By.cssSelector("[data-test^='add-to-cart']");
    private final By cartBadge           = By.cssSelector(".shopping_cart_badge");
    private final By cartLink            = By.cssSelector(".shopping_cart_link");
    private final By cartItems           = By.cssSelector(".cart_item");
    private final By removeButtons       = By.cssSelector("[data-test^='remove']");
    private final By checkoutButton      = By.id("checkout");
    private final By continueShoppingBtn = By.id("continue-shopping");

    public CartPage(WebDriver driver) {
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

    /**
     * Adds the first N products to the cart.
     * Re-fetches buttons each iteration — after clicking, the DOM updates
     * (button changes from "Add to cart" to "Remove"), making old references stale.
     */
    public void addItemsToCart(int count) {
        for (int i = 0; i < count; i++) {
            List<WebElement> buttons = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(addToCartButtons)
            );
            jsClick(buttons.get(i));
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
     * Opens the cart via JS click and waits for the cart URL.
     * JS click bypasses overlay/animation issues blocking Selenium clicks in headless CI.
     */
    public void openCart() {
        WebElement cart = wait.until(ExpectedConditions.presenceOfElementLocated(cartLink));
        jsClick(cart);
        wait.until(ExpectedConditions.urlContains("/cart.html"));
    }

    public boolean isOnCartPage() {
        return driver.getCurrentUrl().contains("/cart.html");
    }

    public int getCartItemCount() {
        return driver.findElements(cartItems).size();
    }

    /**
     * Removes the first item and waits for the DOM to update (staleness check).
     */
    public void removeFirstItem() {
        List<WebElement> buttons = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(removeButtons)
        );
        WebElement firstBtn = buttons.get(0);
        jsClick(firstBtn);
        wait.until(ExpectedConditions.stalenessOf(firstBtn));
    }

    /**
     * Clicks Checkout via JS and waits for Step 1 URL.
     */
    public void proceedToCheckout() {
        wait.until(ExpectedConditions.urlContains("/cart.html"));
        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(checkoutButton));
        jsClick(btn);
        wait.until(ExpectedConditions.urlContains("/checkout-step-one.html"));
    }

    /**
     * Clicks Continue Shopping via JS and waits for inventory URL.
     */
    public void continueShopping() {
        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(continueShoppingBtn));
        jsClick(btn);
        wait.until(ExpectedConditions.urlContains("/inventory.html"));
    }
}
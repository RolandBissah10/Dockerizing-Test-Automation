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
 * Handles product selection and cart management.
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
    private final By itemNames           = By.cssSelector(".inventory_item_name");
    private final By removeButtons       = By.cssSelector("[data-test^='remove']");
    private final By checkoutButton      = By.id("checkout");
    private final By continueShoppingBtn = By.id("continue-shopping");

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    /**
     * Adds the first N products on the inventory page to the cart.
     */
    public void addItemsToCart(int count) {
        List<WebElement> buttons = wait.until(
            ExpectedConditions.presenceOfAllElementsLocatedBy(addToCartButtons)
        );
        int toAdd = Math.min(count, buttons.size());
        for (int i = 0; i < toAdd; i++) {
            buttons.get(i).click();
        }
    }

    public String getCartBadgeCount() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(cartBadge)).getText();
    }

    public boolean isCartBadgeVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(cartBadge)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void openCart() {
        driver.findElement(cartLink).click();
    }

    public int getCartItemCount() {
        try {
            List<WebElement> items = driver.findElements(cartItems);
            return items.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public List<WebElement> getCartItems() {
        return driver.findElements(cartItems);
    }

    public void removeFirstItem() {
        List<WebElement> buttons = driver.findElements(removeButtons);
        if (!buttons.isEmpty()) {
            buttons.get(0).click();
        }
    }

    public void proceedToCheckout() {
        wait.until(ExpectedConditions.elementToBeClickable(checkoutButton)).click();
    }

    public void continueShopping() {
        wait.until(ExpectedConditions.elementToBeClickable(continueShoppingBtn)).click();
    }

    public boolean isOnCartPage() {
        return driver.getCurrentUrl().contains("/cart.html");
    }
}

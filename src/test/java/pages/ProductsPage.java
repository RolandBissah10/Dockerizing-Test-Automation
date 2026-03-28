package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Page Object for the Swag Labs Products (Inventory) page.
 */
public class ProductsPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final JavascriptExecutor js;

    private final By pageTitle        = By.cssSelector(".title");
    private final By productItems     = By.cssSelector(".inventory_item");
    private final By productNames     = By.cssSelector(".inventory_item_name");
    private final By productPrices    = By.cssSelector(".inventory_item_price");
    private final By sortDropdown     = By.cssSelector("[data-test='product-sort-container']");
    private final By cartBadge        = By.cssSelector(".shopping_cart_badge");
    private final By detailName       = By.cssSelector(".inventory_details_name");
    private final By detailDesc       = By.cssSelector(".inventory_details_desc");
    private final By detailPrice      = By.cssSelector(".inventory_details_price");
    private final By backButton       = By.id("back-to-products");
    private final By burgerMenuButton = By.id("react-burger-menu-btn");
    private final By logoutLink       = By.id("logout_sidebar_link");

    public static final String INVENTORY_URL = "https://www.saucedemo.com/inventory.html";

    public ProductsPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(15));
        this.js     = (JavascriptExecutor) driver;
    }

    /**
     * Clicks an element via JavaScript.
     * Bypasses CSS overlays or animations blocking normal Selenium clicks in headless CI.
     */
    private void jsClick(WebElement element) {
        js.executeScript("arguments[0].scrollIntoView(true);", element);
        js.executeScript("arguments[0].click();", element);
    }

    // ── Page state ───────────────────────────────────────────────────────────

    public boolean isDisplayed() {
        try {
            WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(pageTitle));
            return title.getText().equalsIgnoreCase("Products");
        } catch (Exception e) {
            return false;
        }
    }

    // ── Product listing ──────────────────────────────────────────────────────

    public int getProductCount() {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(productItems)).size();
    }

    public List<String> getProductNames() {
        return driver.findElements(productNames)
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public List<String> getProductPrices() {
        return driver.findElements(productPrices)
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public List<Double> getProductPricesAsDouble() {
        return getProductPrices()
                .stream()
                .map(p -> Double.parseDouble(p.replace("$", "")))
                .collect(Collectors.toList());
    }

    // ── Sorting ──────────────────────────────────────────────────────────────

    public void sortBy(String sortValue) {
        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(sortDropdown));
        new Select(dropdown).selectByValue(sortValue);
    }

    // ── Product detail ───────────────────────────────────────────────────────

    public void clickProduct(String productName) {
        List<WebElement> names = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(productNames)
        );
        for (WebElement el : names) {
            if (el.getText().equals(productName)) {
                jsClick(el);
                return;
            }
        }
        throw new RuntimeException("Product not found: " + productName);
    }

    public String getDetailName() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(detailName)).getText();
    }

    public String getDetailDescription() {
        return driver.findElement(detailDesc).getText();
    }

    public String getDetailPrice() {
        return driver.findElement(detailPrice).getText();
    }

    /**
     * Clicks Back to Products via JS and waits for inventory URL.
     */
    public void clickBackToProducts() {
        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(backButton));
        jsClick(btn);
        wait.until(ExpectedConditions.urlToBe(INVENTORY_URL));
    }

    // ── Cart interactions ────────────────────────────────────────────────────

    public void addProductToCart(String productName) {
        String dataTestId = "add-to-cart-" + productName.toLowerCase()
                .replace(" ", "-")
                .replace("(", "")
                .replace(")", "")
                .replace(".", "");
        By addButton = By.cssSelector("[data-test='" + dataTestId + "']");
        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(addButton));
        jsClick(btn);
    }

    public String getCartBadgeCount() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(cartBadge)).getText();
    }

    public boolean isCartBadgeVisible() {
        try {
            return driver.findElement(cartBadge).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // ── Navigation ───────────────────────────────────────────────────────────

    /**
     * Logs out via the burger menu.
     * Opens menu via JS click (bypasses animation), waits for logout link
     * to be clickable, clicks it via JS, then waits for login page URL.
     */
    public void logout() {
        WebElement menu = wait.until(ExpectedConditions.presenceOfElementLocated(burgerMenuButton));
        jsClick(menu);
        WebElement logout = wait.until(ExpectedConditions.elementToBeClickable(logoutLink));
        jsClick(logout);
        wait.until(ExpectedConditions.urlToBe("https://www.saucedemo.com/"));
    }
}
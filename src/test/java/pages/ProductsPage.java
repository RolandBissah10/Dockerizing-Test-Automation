package pages;

import org.openqa.selenium.By;
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
 * Handles product listing, sorting, detail navigation, and logout.
 */
public class ProductsPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Page-level locators
    private final By pageTitle           = By.cssSelector(".title");
    private final By productItems        = By.cssSelector(".inventory_item");
    private final By productNames        = By.cssSelector(".inventory_item_name");
    private final By productPrices       = By.cssSelector(".inventory_item_price");
    private final By sortDropdown        = By.cssSelector("[data-test='product-sort-container']");
    private final By addToCartButtons    = By.cssSelector("[data-test^='add-to-cart']");
    private final By cartBadge           = By.cssSelector(".shopping_cart_badge");

    // Detail page locators
    private final By detailName          = By.cssSelector(".inventory_details_name");
    private final By detailDesc          = By.cssSelector(".inventory_details_desc");
    private final By detailPrice         = By.cssSelector(".inventory_details_price");
    private final By backButton          = By.id("back-to-products");

    // Navigation locators
    private final By burgerMenuButton    = By.id("react-burger-menu-btn");
    private final By logoutLink          = By.id("logout_sidebar_link");

    public static final String INVENTORY_URL = "https://www.saucedemo.com/inventory.html";

    public ProductsPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(15));
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
                el.click();
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
     * Clicks the Back button and waits for the inventory URL to load.
     *
     * FIX: Added urlToBe wait after click. In CI, isDisplayed() was running
     * before the browser navigated back, so the title check returned false.
     */
    public void clickBackToProducts() {
        wait.until(ExpectedConditions.elementToBeClickable(backButton)).click();
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
        wait.until(ExpectedConditions.elementToBeClickable(addButton)).click();
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
     * Logs out via the burger menu and waits for the login page URL.
     *
     * FIX: The burger menu has a slide-open animation. In CI, clicking
     * logoutLink immediately after opening the menu fails because the link
     * isn't yet clickable. We explicitly wait for the link to be clickable,
     * then wait for the URL to confirm logout completed.
     */
    public void logout() {
        wait.until(ExpectedConditions.elementToBeClickable(burgerMenuButton)).click();
        wait.until(ExpectedConditions.elementToBeClickable(logoutLink)).click();
        wait.until(ExpectedConditions.urlToBe("https://www.saucedemo.com/"));
    }
}
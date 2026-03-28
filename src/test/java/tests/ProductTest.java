package tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.LoginPage;
import pages.ProductsPage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for the Swag Labs Products (Inventory) page.
 * Covers product listing, sorting, product detail, and cart interactions
 * from the inventory view.
 */
@DisplayName("Products Page Tests")
public class ProductTest extends BaseTest {

    private ProductsPage productsPage;

    @BeforeEach
    public void loginAndNavigate() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.loginAs(TestData.STANDARD_USER, TestData.VALID_PASSWORD);

        productsPage = new ProductsPage(driver);
    }

    // ── Product listing ──────────────────────────────────────────────────────

    @Test
    @DisplayName("TC21 - Products page should display the correct title")
    public void testProductsPageTitle() {
        assertTrue(productsPage.isDisplayed(),
            "Products page title should read 'Products'");
    }

    @Test
    @DisplayName("TC22 - Products page should display all 6 products")
    public void testAllProductsAreDisplayed() {
        int count = productsPage.getProductCount();
        assertEquals(TestData.TOTAL_PRODUCT_COUNT, count,
            "Expected " + TestData.TOTAL_PRODUCT_COUNT + " products but found " + count);
    }

    @Test
    @DisplayName("TC23 - All expected product names should be present in the listing")
    public void testExpectedProductNamesArePresent() {
        List<String> names = productsPage.getProductNames();

        assertTrue(names.contains(TestData.PRODUCT_BACKPACK),   "Missing: " + TestData.PRODUCT_BACKPACK);
        assertTrue(names.contains(TestData.PRODUCT_BIKE_LIGHT), "Missing: " + TestData.PRODUCT_BIKE_LIGHT);
        assertTrue(names.contains(TestData.PRODUCT_BOLT_SHIRT), "Missing: " + TestData.PRODUCT_BOLT_SHIRT);
        assertTrue(names.contains(TestData.PRODUCT_FLEECE),     "Missing: " + TestData.PRODUCT_FLEECE);
        assertTrue(names.contains(TestData.PRODUCT_ONESIE),     "Missing: " + TestData.PRODUCT_ONESIE);
        assertTrue(names.contains(TestData.PRODUCT_RED_SHIRT),  "Missing: " + TestData.PRODUCT_RED_SHIRT);
    }

    @Test
    @DisplayName("TC24 - All products should have a displayed price")
    public void testAllProductsHavePrice() {
        List<String> prices = productsPage.getProductPrices();

        assertFalse(prices.isEmpty(), "Price list should not be empty");
        assertEquals(TestData.TOTAL_PRODUCT_COUNT, prices.size(),
            "Every product should have a price");
        for (String price : prices) {
            assertTrue(price.startsWith("$"),
                "Price should start with '$', got: " + price);
        }
    }

    // ── Sorting ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC25 - Sort A-Z should order products alphabetically ascending")
    public void testSortAtoZ() {
        productsPage.sortBy(TestData.SORT_AZ);

        List<String> names = productsPage.getProductNames();
        for (int i = 0; i < names.size() - 1; i++) {
            assertTrue(names.get(i).compareToIgnoreCase(names.get(i + 1)) <= 0,
                "Products not in A-Z order at index " + i + ": '"
                + names.get(i) + "' should come before '" + names.get(i + 1) + "'");
        }
    }

    @Test
    @DisplayName("TC26 - Sort Z-A should order products alphabetically descending")
    public void testSortZtoA() {
        productsPage.sortBy(TestData.SORT_ZA);

        List<String> names = productsPage.getProductNames();
        for (int i = 0; i < names.size() - 1; i++) {
            assertTrue(names.get(i).compareToIgnoreCase(names.get(i + 1)) >= 0,
                "Products not in Z-A order at index " + i + ": '"
                + names.get(i) + "' should come after '" + names.get(i + 1) + "'");
        }
    }

    @Test
    @DisplayName("TC27 - Sort Low-to-High should order products by price ascending")
    public void testSortPriceLowToHigh() {
        productsPage.sortBy(TestData.SORT_LOHI);

        List<Double> prices = productsPage.getProductPricesAsDouble();
        for (int i = 0; i < prices.size() - 1; i++) {
            assertTrue(prices.get(i) <= prices.get(i + 1),
                "Prices not in low-to-high order at index " + i + ": "
                + prices.get(i) + " > " + prices.get(i + 1));
        }
    }

    @Test
    @DisplayName("TC28 - Sort High-to-Low should order products by price descending")
    public void testSortPriceHighToLow() {
        productsPage.sortBy(TestData.SORT_HILO);

        List<Double> prices = productsPage.getProductPricesAsDouble();
        for (int i = 0; i < prices.size() - 1; i++) {
            assertTrue(prices.get(i) >= prices.get(i + 1),
                "Prices not in high-to-low order at index " + i + ": "
                + prices.get(i) + " < " + prices.get(i + 1));
        }
    }

    // ── Product detail ───────────────────────────────────────────────────────

    @Test
    @DisplayName("TC29 - Clicking a product should navigate to its detail page")
    public void testProductDetailPageOpens() {
        productsPage.clickProduct(TestData.PRODUCT_BACKPACK);

        String detailName = productsPage.getDetailName();
        assertEquals(TestData.PRODUCT_BACKPACK, detailName,
            "Detail page name should match the product clicked");
    }

    @Test
    @DisplayName("TC30 - Product detail page should show name, description, and price")
    public void testProductDetailPageShowsFullInfo() {
        productsPage.clickProduct(TestData.PRODUCT_BACKPACK);

        assertFalse(productsPage.getDetailName().isEmpty(),        "Detail name should not be empty");
        assertFalse(productsPage.getDetailDescription().isEmpty(), "Detail description should not be empty");
        assertFalse(productsPage.getDetailPrice().isEmpty(),       "Detail price should not be empty");
        assertTrue(productsPage.getDetailPrice().startsWith("$"),  "Detail price should start with '$'");
    }

    @Test
    @DisplayName("TC31 - Back button on detail page should return to products listing")
    public void testBackButtonReturnsToProductsPage() {
        productsPage.clickProduct(TestData.PRODUCT_BACKPACK);
        productsPage.clickBackToProducts();

        assertTrue(productsPage.isDisplayed(),
            "Clicking Back should return to the Products listing page");
        assertEquals(driver.getCurrentUrl(), ProductsPage.INVENTORY_URL,
            "URL should be the inventory page after clicking back");
    }

    // ── Cart from inventory ──────────────────────────────────────────────────

    @Test
    @DisplayName("TC32 - Adding a product from inventory should update the cart badge")
    public void testAddProductUpdatesCartBadge() {
        productsPage.addProductToCart(TestData.PRODUCT_BACKPACK);

        assertTrue(productsPage.isCartBadgeVisible(), "Cart badge should be visible");
        assertEquals("1", productsPage.getCartBadgeCount(), "Cart badge count should be 1");
    }

    @Test
    @DisplayName("TC33 - Logout from products page should redirect to login page")
    public void testLogoutFromProductsPage() {
        productsPage.logout();

        assertEquals(TestData.BASE_URL + "/", driver.getCurrentUrl(),
            "Logging out should redirect to the login page");
    }
}

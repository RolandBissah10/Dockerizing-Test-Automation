package com.swaglabs.tests;

import com.swaglabs.pages.CartPage;
import com.swaglabs.pages.LoginPage;
import com.swaglabs.base.BaseTest;
import com.swaglabs.data.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for Swag Labs Shopping Cart functionality.
 * Covers adding items, badge count, removing items, and navigating to checkout.
 */
@DisplayName("Cart Tests")
public class CartTest extends BaseTest {

    private LoginPage loginPage;
    private CartPage cartPage;

    @BeforeEach
    public void initAndLogin() {
        loginPage = new LoginPage(driver);
        cartPage  = new CartPage(driver);

        loginPage.open();
        loginPage.loginAs(TestData.STANDARD_USER, TestData.VALID_PASSWORD);
    }

    @Test
    @DisplayName("TC07 - Adding one item should show badge count of 1")
    public void testAddSingleItemToCart() {
        cartPage.addItemsToCart(1);

        assertTrue(cartPage.isCartBadgeVisible(), "Cart badge should be visible after adding an item");
        assertEquals("1", cartPage.getCartBadgeCount(), "Cart badge should display 1");
    }

    @Test
    @DisplayName("TC08 - Adding multiple items should reflect correct badge count")
    public void testAddMultipleItemsToCart() {
        cartPage.addItemsToCart(3);

        assertEquals("3", cartPage.getCartBadgeCount(),
            "Cart badge count should reflect the number of items added");
    }

    @Test
    @DisplayName("TC09 - Cart page should list all added items")
    public void testCartPageShowsAddedItems() {
        cartPage.addItemsToCart(2);
        cartPage.openCart();

        assertTrue(cartPage.isOnCartPage(), "Should navigate to the cart page");
        assertEquals(2, cartPage.getCartItemCount(),
            "Cart should contain exactly 2 items");
    }

    @Test
    @DisplayName("TC10 - Removing an item from cart should decrease item count")
    public void testRemoveItemFromCart() {
        cartPage.addItemsToCart(2);
        cartPage.openCart();

        assertEquals(2, cartPage.getCartItemCount(), "Cart should start with 2 items");

        cartPage.removeFirstItem();

        assertEquals(1, cartPage.getCartItemCount(),
            "Cart should contain 1 item after removal");
    }

    @Test
    @DisplayName("TC11 - Clicking 'Continue Shopping' from cart returns to inventory")
    public void testContinueShoppingFromCart() {
        cartPage.addItemsToCart(1);
        cartPage.openCart();
        cartPage.continueShopping();

        assertTrue(driver.getCurrentUrl().contains("/inventory.html"),
            "Should return to the inventory page after clicking Continue Shopping");
    }

    @Test
    @DisplayName("TC12 - Cart should be empty when no items are added")
    public void testEmptyCart() {
        cartPage.openCart();

        assertTrue(cartPage.isOnCartPage(), "Should navigate to the cart page");
        assertEquals(0, cartPage.getCartItemCount(),
            "Cart should be empty when no items were added");
        assertFalse(cartPage.isCartBadgeVisible(),
            "Cart badge should not be visible when cart is empty");
    }
}

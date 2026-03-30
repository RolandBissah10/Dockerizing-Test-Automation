package com.swaglabs.tests;

import com.swaglabs.pages.CartPage;
import com.swaglabs.pages.CheckoutPage;
import com.swaglabs.pages.LoginPage;
import com.swaglabs.base.BaseTest;
import com.swaglabs.data.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the Swag Labs Checkout flow.
 * Covers the full purchase journey from cart to order confirmation.
 */
@DisplayName("Checkout Tests")
public class CheckoutTest extends BaseTest {

    private CartPage cartPage;
    private CheckoutPage checkoutPage;

    @BeforeEach
    public void initAndPrepareCart() {
        LoginPage loginPage = new LoginPage(driver);
        cartPage            = new CartPage(driver);
        checkoutPage        = new CheckoutPage(driver);

        // Login → add items → open cart → proceed to checkout
        loginPage.open();
        loginPage.loginAs(TestData.STANDARD_USER, TestData.VALID_PASSWORD);
        cartPage.addItemsToCart(2);
        cartPage.openCart();
        cartPage.proceedToCheckout();
    }

    @Test
    @DisplayName("TC13 - Checkout Step 1 should be displayed after clicking Checkout")
    public void testCheckoutStep1IsDisplayed() {
        assertTrue(checkoutPage.isOnStep1(),
            "Should land on Checkout Step 1 after clicking Checkout button");
    }

    @Test
    @DisplayName("TC14 - Valid customer info should advance to Step 2")
    public void testValidInfoProceedsToStep2() {
        checkoutPage.fillCustomerInfo(TestData.FIRST_NAME, TestData.LAST_NAME, TestData.POSTAL_CODE);
        checkoutPage.clickContinue();

        assertTrue(checkoutPage.isOnStep2(),
            "Should proceed to Step 2 after entering valid customer information");
    }

    @Test
    @DisplayName("TC15 - Missing first name should show validation error")
    public void testMissingFirstNameShowsError() {
        checkoutPage.fillCustomerInfo(TestData.EMPTY, TestData.LAST_NAME, TestData.POSTAL_CODE);
        checkoutPage.clickContinue();

        String error = checkoutPage.getErrorMessage();
        assertTrue(error.contains(TestData.ERR_FIRST_NAME),
            "Expected '" + TestData.ERR_FIRST_NAME + "' error, got: " + error);
    }

    @Test
    @DisplayName("TC16 - Missing last name should show validation error")
    public void testMissingLastNameShowsError() {
        checkoutPage.fillCustomerInfo(TestData.FIRST_NAME, TestData.EMPTY, TestData.POSTAL_CODE);
        checkoutPage.clickContinue();

        String error = checkoutPage.getErrorMessage();
        assertTrue(error.contains(TestData.ERR_LAST_NAME),
            "Expected '" + TestData.ERR_LAST_NAME + "' error, got: " + error);
    }

    @Test
    @DisplayName("TC17 - Missing postal code should show validation error")
    public void testMissingPostalCodeShowsError() {
        checkoutPage.fillCustomerInfo(TestData.FIRST_NAME, TestData.LAST_NAME, TestData.EMPTY);
        checkoutPage.clickContinue();

        String error = checkoutPage.getErrorMessage();
        assertTrue(error.contains(TestData.ERR_POSTAL_CODE),
            "Expected '" + TestData.ERR_POSTAL_CODE + "' error, got: " + error);
    }

    @Test
    @DisplayName("TC18 - Step 2 overview should display item total, tax, and total")
    public void testStep2DisplaysPriceSummary() {
        checkoutPage.fillCustomerInfo(TestData.ALT_FIRST_NAME, TestData.ALT_LAST_NAME, TestData.ALT_POSTAL_CODE);
        checkoutPage.clickContinue();

        assertTrue(checkoutPage.isOnStep2(), "Should be on Step 2");

        assertFalse(checkoutPage.getItemTotal().isEmpty(),  "Item total should be displayed");
        assertFalse(checkoutPage.getTax().isEmpty(),        "Tax should be displayed");
        assertFalse(checkoutPage.getOrderTotal().isEmpty(), "Order total should be displayed");
    }

    @Test
    @DisplayName("TC19 - Completing checkout should show order confirmation")
    public void testCompleteCheckoutShowsConfirmation() {
        checkoutPage.fillCustomerInfo(TestData.ALT_FIRST_NAME, TestData.ALT_LAST_NAME, TestData.ALT_POSTAL_CODE);
        checkoutPage.clickContinue();
        checkoutPage.clickFinish();

        assertTrue(checkoutPage.isOnConfirmationPage(),
            "Should navigate to the order confirmation page");
        assertTrue(checkoutPage.getConfirmationHeader().contains(TestData.CONFIRMATION_HEADER),
            "Confirmation header mismatch: " + checkoutPage.getConfirmationHeader());
    }

    @Test
    @DisplayName("TC20 - Back to Products button on confirmation returns to inventory")
    public void testBackToProductsFromConfirmation() {
        checkoutPage.fillCustomerInfo(TestData.ALT_FIRST_NAME, TestData.ALT_LAST_NAME, TestData.ALT_POSTAL_CODE);
        checkoutPage.clickContinue();
        checkoutPage.clickFinish();
        checkoutPage.clickBackHome();

        assertTrue(driver.getCurrentUrl().contains("/inventory.html"),
            "Clicking 'Back Home' should return the user to the inventory page");
    }
}

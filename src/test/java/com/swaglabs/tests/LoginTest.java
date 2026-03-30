package com.swaglabs.tests;

import com.swaglabs.pages.LoginPage;
import com.swaglabs.base.BaseTest;
import com.swaglabs.data.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DisplayName("Login Tests")
public class LoginTest extends BaseTest {

    private LoginPage loginPage;

    @BeforeEach
    public void initPage() {
        loginPage = new LoginPage(driver);
        loginPage.open();
    }

    @Test
    @DisplayName("TC01 - Valid credentials should redirect to inventory page")
    public void testValidLogin() {
        loginPage.loginAs(TestData.STANDARD_USER, TestData.VALID_PASSWORD);

        assertTrue(loginPage.isInventoryPageDisplayed(),
            "Expected to land on the Products page after valid login");
    }

    @Test
    @DisplayName("TC02 - Invalid password should display error message")
    public void testInvalidPassword() {
        loginPage.loginAs(TestData.STANDARD_USER, TestData.WRONG_PASSWORD);

        String error = loginPage.getErrorMessage();
        assertFalse(error.isEmpty(), "Expected an error message for wrong password");
        assertTrue(error.contains(TestData.ERR_INVALID_CREDS),
            "Error message text mismatch: " + error);
    }

    @Test
    @DisplayName("TC03 - Empty username should display error message")
    public void testEmptyUsername() {
        loginPage.loginAs(TestData.EMPTY, TestData.VALID_PASSWORD);

        String error = loginPage.getErrorMessage();
        assertTrue(error.contains(TestData.ERR_USERNAME_REQUIRED),
            "Expected '" + TestData.ERR_USERNAME_REQUIRED + "' error, got: " + error);
    }

    @Test
    @DisplayName("TC04 - Empty password should display error message")
    public void testEmptyPassword() {
        loginPage.loginAs(TestData.STANDARD_USER, TestData.EMPTY);

        String error = loginPage.getErrorMessage();
        assertTrue(error.contains(TestData.ERR_PASSWORD_REQUIRED),
            "Expected '" + TestData.ERR_PASSWORD_REQUIRED + "' error, got: " + error);
    }

    @Test
    @DisplayName("TC05 - Locked-out user should see locked-out error")
    public void testLockedOutUser() {
        loginPage.loginAs(TestData.LOCKED_OUT_USER, TestData.VALID_PASSWORD);

        String error = loginPage.getErrorMessage();
        assertTrue(error.contains(TestData.ERR_LOCKED_OUT),
            "Expected locked-out error message, got: " + error);
    }


}

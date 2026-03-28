package tests.data;

/**
 * Central repository for all test data used across the suite.
 *
 * Benefits:
 * - Single source of truth — change a value here, it updates everywhere
 * - Easier maintenance and readability in test classes
 * - Mirrors real-world practice of separating data from test logic
 */
public final class TestData {

    // Prevent instantiation — this is a static constants class
    private TestData() {}

    // ── Base URL ─────────────────────────────────────────────────────────────
    public static final String BASE_URL = "https://www.saucedemo.com";

    // ── Valid credentials ────────────────────────────────────────────────────
    public static final String STANDARD_USER     = "standard_user";
    public static final String VALID_PASSWORD    = "secret_sauce";

    // ── Invalid / edge-case credentials ─────────────────────────────────────
    public static final String LOCKED_OUT_USER   = "locked_out_user";
    public static final String WRONG_PASSWORD    = "wrong_password";
    public static final String EMPTY             = "";

    // ── Customer info (checkout) ─────────────────────────────────────────────
    public static final String FIRST_NAME        = "John";
    public static final String LAST_NAME         = "Doe";
    public static final String POSTAL_CODE       = "12345";

    public static final String ALT_FIRST_NAME    = "Jane";
    public static final String ALT_LAST_NAME     = "Smith";
    public static final String ALT_POSTAL_CODE   = "90210";

    // ── Products ─────────────────────────────────────────────────────────────
    public static final String PRODUCT_BACKPACK  = "Sauce Labs Backpack";
    public static final String PRODUCT_BIKE_LIGHT = "Sauce Labs Bike Light";
    public static final String PRODUCT_BOLT_SHIRT = "Sauce Labs Bolt T-Shirt";
    public static final String PRODUCT_FLEECE    = "Sauce Labs Fleece Jacket";
    public static final String PRODUCT_ONESIE    = "Sauce Labs Onesie";
    public static final String PRODUCT_RED_SHIRT = "Test.allTheThings() T-Shirt (Red)";

    public static final int TOTAL_PRODUCT_COUNT  = 6;

    // ── Sort options ─────────────────────────────────────────────────────────
    public static final String SORT_AZ           = "az";    // Name (A to Z)
    public static final String SORT_ZA           = "za";    // Name (Z to A)
    public static final String SORT_LOHI         = "lohi";  // Price (low to high)
    public static final String SORT_HILO         = "hilo";  // Price (high to low)

    // ── Expected error messages ───────────────────────────────────────────────
    public static final String ERR_USERNAME_REQUIRED = "Username is required";
    public static final String ERR_PASSWORD_REQUIRED = "Password is required";
    public static final String ERR_INVALID_CREDS     = "Username and password do not match";
    public static final String ERR_LOCKED_OUT        = "Sorry, this user has been locked out";

    public static final String ERR_FIRST_NAME        = "First Name is required";
    public static final String ERR_LAST_NAME         = "Last Name is required";
    public static final String ERR_POSTAL_CODE       = "Postal Code is required";

    // ── Confirmation ─────────────────────────────────────────────────────────
    public static final String CONFIRMATION_HEADER   = "Thank you for your order";
}

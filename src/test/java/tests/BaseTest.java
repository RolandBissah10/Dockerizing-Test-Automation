package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

/**
 * Base test class that handles WebDriver lifecycle.
 * All test classes extend this to get a fresh browser per test.
 *
 * Supports both local Windows/Mac runs and headless Docker/CI runs.
 * Set environment variable HEADLESS=false to run with a visible browser locally.
 */
public class BaseTest {

    protected WebDriver driver;

    @BeforeEach
    public void setUp() {
        // WebDriverManager auto-downloads the correct ChromeDriver version
        // that matches the installed Chrome — this fixes version mismatch errors
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();

        // ── Core headless flags (required in Docker & CI) ──────────────────
        options.addArguments("--headless=new");           // Use new headless mode (Chrome 112+)
        options.addArguments("--no-sandbox");             // Required in Docker / root user
        options.addArguments("--disable-dev-shm-usage"); // Prevents /dev/shm memory crashes

        // ── Renderer / GPU flags ────────────────────────────────────────────
        options.addArguments("--disable-gpu");            // Disable GPU (headless stability)
        options.addArguments("--disable-software-rasterizer");
        options.addArguments("--disable-extensions");

        // ── Window & display ────────────────────────────────────────────────
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--start-maximized");

        // ── Stability & performance flags ────────────────────────────────────
        options.addArguments("--disable-background-networking");
        options.addArguments("--disable-default-apps");
        options.addArguments("--disable-sync");
        options.addArguments("--no-first-run");
        options.addArguments("--no-default-browser-check");

        // ── Fix "unable to connect to renderer" on Windows ──────────────────
        // Removing --remote-debugging-port avoids port-conflict issues on Windows
        // where multiple test processes may try to bind the same port
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().window().maximize();
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}

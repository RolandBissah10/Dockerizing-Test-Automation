# Swag Labs — Dockerized Selenium + JUnit Test Suite

Automated UI test suite for [Swag Labs](https://www.saucedemo.com), covering
**Login**, **Cart**, and **Checkout** flows using **Selenium WebDriver + JUnit 5**,
packaged in a **Docker container** and wired into a **GitHub Actions CI pipeline**.

---

## Project Structure

```
swag-labs-docker-tests/
├── .github/
│   └── workflows/
│       └── ci.yml                     # GitHub Actions CI pipeline
├── src/
│   └── test/
│       └── java/
│           ├── pages/
│           │   ├── LoginPage.java      # Login page interactions
│           │   ├── CartPage.java       # Cart & inventory interactions
│           │   └── CheckoutPage.java   # Checkout flow interactions
│           └── tests/
│               ├── BaseTest.java       # WebDriver setup / teardown
│               ├── LoginTest.java      # 6 login test cases (TC01–TC06)
│               ├── CartTest.java       # 6 cart test cases   (TC07–TC12)
│               └── CheckoutTest.java   # 8 checkout cases    (TC13–TC20)
├── Dockerfile                          # Multi-stage Docker build
├── .dockerignore
├── pom.xml                             # Maven dependencies
└── README.md
```

---

## Test Coverage (20 test cases)

| Suite         | Cases | What's Tested                                              |
|---------------|-------|------------------------------------------------------------|
| LoginTest     | 6     | Valid login, bad password, empty fields, locked-out user   |
| CartTest      | 6     | Add 1/many items, badge count, remove item, empty cart     |
| CheckoutTest  | 8     | Step 1 validation, Step 2 summary, full purchase, back home|

---

## Prerequisites

| Tool              | Minimum Version |
|-------------------|-----------------|
| Docker            | 20.x+           |
| Java (local run)  | 11+             |
| Maven (local run) | 3.9+            |

---

## Running Tests

### Option A — Inside Docker (recommended)

```bash
# 1. Build the Docker image
docker build -t swag-labs-tests .

# 2. Run the full test suite
docker run --rm swag-labs-tests
```

Test results are printed to stdout. Maven exits with code `0` (pass) or `1` (fail).

---

### Option B — Locally (without Docker)

> Requires Chrome + Java 11 + Maven installed locally.

```bash
# Run all tests
mvn test

# Run a specific test class
mvn test -Dtest=LoginTest

# Run a single test method
mvn test -Dtest=LoginTest#testValidLogin
```

---

## CI/CD Pipeline

The GitHub Actions workflow (`.github/workflows/ci.yml`) triggers on:
- Every push to `main` or `develop`
- Every pull request targeting `main`
- Manual dispatch from the Actions tab

**Pipeline steps:**
1. Checkout code
2. `docker build` — builds the image
3. `docker run` — executes tests inside the container
4. Extracts Surefire XML reports from the container
5. Publishes results as a GitHub Check (pass/fail badge)
6. Uploads reports as a downloadable build artifact (14-day retention)

---

## Chrome in Docker

Chrome runs in **headless mode** with these required flags (set in `BaseTest.java`):

```
--headless              # No display server needed
--no-sandbox            # Required inside Docker
--disable-dev-shm-usage # Prevents /dev/shm memory errors
--disable-gpu           # Stability in headless mode
```

---

## Viewing Test Reports

After a local run, open the HTML report:

```bash
open target/surefire-reports/*.txt
# or view XML
cat target/surefire-reports/TEST-tests.LoginTest.xml
```

After a CI run, download the `surefire-reports-<sha>` artifact from the
**Actions → your workflow run → Artifacts** section on GitHub.

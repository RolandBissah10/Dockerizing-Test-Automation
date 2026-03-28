# ─────────────────────────────────────────────────────────────────────────────
# Stage 1: Build — compile the Maven project and download all dependencies
# ─────────────────────────────────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-11 AS builder

WORKDIR /app

# Copy pom.xml first to leverage Docker layer caching for dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the rest of the source code
COPY src ./src

# Compile the project (skip tests at build stage — we run them at container start)
RUN mvn compile test-compile -B

# ─────────────────────────────────────────────────────────────────────────────
# Stage 2: Test Runner — install Chrome and run the test suite
# ─────────────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:11-jdk

LABEL maintainer="QA Team"
LABEL description="Swag Labs Selenium + JUnit test suite running in Docker"

# Install system dependencies and Google Chrome
RUN apt-get update && apt-get install -y \
    wget \
    curl \
    gnupg \
    unzip \
    fonts-liberation \
    libasound2 \
    libatk-bridge2.0-0 \
    libatk1.0-0 \
    libatspi2.0-0 \
    libcups2 \
    libdbus-1-3 \
    libdrm2 \
    libgbm1 \
    libgtk-3-0 \
    libnspr4 \
    libnss3 \
    libwayland-client0 \
    libxcomposite1 \
    libxdamage1 \
    libxfixes3 \
    libxkbcommon0 \
    libxrandr2 \
    xdg-utils \
    --no-install-recommends \
    && rm -rf /var/lib/apt/lists/*

# Install Google Chrome (stable)
RUN wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" \
       > /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update \
    && apt-get install -y google-chrome-stable \
    && rm -rf /var/lib/apt/lists/*

# Install Maven (needed to run tests in this stage)
RUN wget -q https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz \
    && tar -xzf apache-maven-3.9.6-bin.tar.gz -C /opt \
    && ln -s /opt/apache-maven-3.9.6/bin/mvn /usr/local/bin/mvn \
    && rm apache-maven-3.9.6-bin.tar.gz

# Set working directory
WORKDIR /app

# Copy compiled project and Maven cache from builder stage
COPY --from=builder /root/.m2 /root/.m2
COPY --from=builder /app /app

# Environment variables for headless Chrome in Docker
ENV JAVA_OPTS="-Xmx512m"
ENV DISPLAY=""

# Expose a label showing Chrome version (informational)
RUN google-chrome --version

# Entry point: run the full test suite when the container starts
# Tests are run in batch (non-interactive) mode
ENTRYPOINT ["mvn", "test", "-B", "--no-transfer-progress"]

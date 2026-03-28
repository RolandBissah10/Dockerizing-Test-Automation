# ─────────────────────────────────────────────────────────────────────────────
# Stage 1: Build — compile the Maven project and download dependencies
# ─────────────────────────────────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-11 AS builder

WORKDIR /app

# Copy pom.xml first so Docker caches the dependency download layer separately.
# This means re-builds only re-download dependencies if pom.xml actually changed.
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and compile (tests are run in Stage 2, not here)
COPY src ./src
RUN mvn compile test-compile -B

# ─────────────────────────────────────────────────────────────────────────────
# Stage 2: Test Runner — installs Chrome and runs the test suite
# ─────────────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:11-jdk-jammy

LABEL maintainer="QA Team"
LABEL description="Swag Labs Selenium + JUnit test suite running in Docker"

# Install Chrome's system-level dependencies
RUN apt-get update && apt-get install -y \
    wget curl gnupg unzip \
    fonts-liberation \
    libasound2 libatk-bridge2.0-0 libatk1.0-0 libatspi2.0-0 \
    libcups2 libdbus-1-3 libdrm2 libgbm1 libgtk-3-0 \
    libnspr4 libnss3 libwayland-client0 \
    libxcomposite1 libxdamage1 libxfixes3 libxkbcommon0 libxrandr2 \
    xdg-utils ca-certificates \
    --no-install-recommends \
    && rm -rf /var/lib/apt/lists/*

# Install Google Chrome stable
RUN mkdir -p /etc/apt/keyrings \
    && curl -fsSL https://dl.google.com/linux/linux_signing_key.pub \
       | gpg --dearmor -o /etc/apt/keyrings/google.gpg \
    && echo "deb [arch=amd64 signed-by=/etc/apt/keyrings/google.gpg] \
       http://dl.google.com/linux/chrome/deb/ stable main" \
       > /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update \
    && apt-get install -y google-chrome-stable \
    && rm -rf /var/lib/apt/lists/*

# Install Maven (needed to run mvn test inside the container)
RUN wget -q https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz \
    && tar -xzf apache-maven-3.9.6-bin.tar.gz -C /opt \
    && ln -s /opt/apache-maven-3.9.6/bin/mvn /usr/local/bin/mvn \
    && rm apache-maven-3.9.6-bin.tar.gz

WORKDIR /app

# Bring in the compiled code and cached Maven dependencies from Stage 1
COPY --from=builder /root/.m2 /root/.m2
COPY --from=builder /app /app

# Java heap size — 512MB is enough for running Selenium tests
ENV JAVA_OPTS="-Xmx512m"

# --disable-dev-shm-usage → tells Chrome to use /tmp instead of /dev/shm.
# This is a fallback safety net for when the container's shared memory is low.
# The primary fix is --shm-size=2g passed at docker run time in the CI pipeline.
# Having both ensures Chrome is stable regardless of how the container is launched.
ENV CHROME_OPTIONS="--headless=new --no-sandbox --disable-dev-shm-usage --disable-gpu"

# Confirm Chrome installed correctly (visible in build logs)
RUN google-chrome --version

# Start the test suite automatically when the container runs
ENTRYPOINT ["mvn", "test", "-B", "--no-transfer-progress"]
FROM maven:3.9.6-eclipse-temurin-11 AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn compile test-compile -B

FROM eclipse-temurin:11-jdk-jammy

LABEL maintainer="QA Team"
LABEL description="Swag Labs Selenium + JUnit + Allure test suite in Docker"

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

RUN mkdir -p /etc/apt/keyrings \
    && curl -fsSL https://dl.google.com/linux/linux_signing_key.pub \
       | gpg --dearmor -o /etc/apt/keyrings/google.gpg \
    && echo "deb [arch=amd64 signed-by=/etc/apt/keyrings/google.gpg] http://dl.google.com/linux/chrome/deb/ stable main" \
       > /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update \
    && apt-get install -y google-chrome-stable \
    && rm -rf /var/lib/apt/lists/*

RUN wget -q https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz \
    && tar -xzf apache-maven-3.9.6-bin.tar.gz -C /opt \
    && ln -s /opt/apache-maven-3.9.6/bin/mvn /usr/local/bin/mvn \
    && rm apache-maven-3.9.6-bin.tar.gz

WORKDIR /app

COPY --from=builder /root/.m2 /root/.m2
COPY --from=builder /app /app

ENV JAVA_OPTS="-Xmx512m"
ENV CHROME_OPTIONS="--headless=new --no-sandbox --disable-dev-shm-usage --disable-gpu"

RUN google-chrome --version

ENTRYPOINT ["mvn", "clean", "test", "-Dallure.results.directory=target/allure-results", "-B", "--no-transfer-progress"]
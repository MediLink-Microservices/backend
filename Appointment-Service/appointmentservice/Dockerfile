# Use JDK 21
FROM eclipse-temurin:21-jdk-jammy

# Install maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy source
COPY pom.xml .
COPY src ./src

# Build jar (skip tests for Docker build stability)
RUN mvn -B clean package -Dmaven.test.skip=true && mv target/*.jar app.jar

# Expose service port
EXPOSE 8084

# Run app
ENTRYPOINT ["java", "-jar", "app.jar"]


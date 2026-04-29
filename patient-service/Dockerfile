FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -B clean package -Dmaven.test.skip=true && mv target/*.jar app.jar

FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY --from=build /app/app.jar ./app.jar

EXPOSE 8086

ENTRYPOINT ["java", "-jar", "app.jar"]

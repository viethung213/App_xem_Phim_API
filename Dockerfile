# Stage 1: Build with Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml ./
RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests

# Stage 2: Run the app with JDK
FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081

ENV MEILI_HOST=https://meili-search-long-water-6444.fly.dev/
ENV MEILI_API_KEY=chiepdepvlsuperkey

CMD ["java", "-jar", "app.jar"]

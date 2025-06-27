# 1/ Builder : on compile avec Maven + JDK 17
FROM maven:3.9.3-eclipse-temurin-17 AS builder
WORKDIR /app

# On copie d'abord le POM et les sources
COPY pom.xml .
COPY src ./src

# On construit le JAR sans les tests
RUN mvn clean package -DskipTests

# 2/ Runtime : on lance le JAR dans un JRE plus léger
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# On importe le JAR produit au builder
COPY --from=builder /app/target/BackendPI-0.0.1-SNAPSHOT.jar app.jar

# On expose le port que vous avez configuré (8082)
EXPOSE 8082

# Lancement de l’application
ENTRYPOINT ["java", "-jar", "app.jar"]

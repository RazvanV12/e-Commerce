# ===== STAGE 1: build =====
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# copier  fisiere necesare Maven necesare pentru Maven
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw mvnw

RUN chmod +x mvnw

# descarcare dependinte
RUN ./mvnw -q -DskipTests dependency:go-offline

# copiere cod
COPY src src

# build aplicatie
RUN ./mvnw -q -DskipTests clean package

# ===== STAGE 2: runtime =====
FROM eclipse-temurin:21-jre
WORKDIR /app

# copiem JAR-ul construit
COPY --from=build /app/target/*.jar app.jar

# portul pe care asculta aplicatia
EXPOSE 8081

# pornire aplicatie
ENTRYPOINT ["java","-jar","/app/app.jar"]


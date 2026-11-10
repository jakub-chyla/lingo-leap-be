
# syntax=docker/dockerfile:1.7

# ==========================================
# BUILD STAGE
# ==========================================
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Najpierw kopiujemy konfigurację Maven.
# Dzięki temu zmiana kodu nie unieważnia cache zależności.
COPY pom.xml .

# Cache zależności Maven pomiędzy buildami.
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -ntp dependency:go-offline

# Dopiero teraz kopiujemy kod źródłowy.
COPY src ./src

# Kompilacja aplikacji.
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -ntp package -DskipTests


# ==========================================
# RUNTIME STAGE
# ==========================================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Nie uruchamiamy aplikacji jako root.
RUN useradd --system --uid 10001 --no-create-home appuser

COPY --from=build --chown=10001:10001 \
    /app/target/lingo-leap-0.0.1-SNAPSHOT.jar \
    /app/lingo-leap.jar

USER 10001:10001

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/lingo-leap.jar"]

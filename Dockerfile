#FROM maven:3.8.2-eclipse-temurin-17 AS build
#COPY . .
#RUN mvn clean package -Pprod -DskipTests
#
#FROM openjdk:17-jdk-slim
#COPY --from=build /target/lingo-leap-0.0.1-SNAPSHOT.jar lingo-leap.jar
#EXPOSE 8080
#ENTRYPOINT ["java", "-Dspring.profiles.active=default", "-jar", "lingo-leap.jar"]


FROM maven:3.8.2-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -Pprod -DskipTests

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/lingo-leap-0.0.1-SNAPSHOT.jar lingo-leap.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=default", "-jar", "lingo-leap.jar"]

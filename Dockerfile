FROM maven:3.8.5-openjdk-17-slim AS builder

WORKDIR /build
COPY /app /build

RUN mvn clean package -DskipTests -Dspring.profiles.active=swagger

FROM openjdk:17-alpine3.13
COPY --from=builder /build/target/*.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar", "-Ddebug", "-Xmx128m"]
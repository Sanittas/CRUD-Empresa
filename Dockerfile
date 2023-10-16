FROM maven:3.8.5-openjdk-17-slim AS builder

WORKDIR /build
COPY /app /build

RUN mvn clean package -DskipTests

FROM openjdk:22-ea-18-jdk-slim
COPY --from=builder /build/target/*.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar", "-Ddebug", "-Xmx128m"]
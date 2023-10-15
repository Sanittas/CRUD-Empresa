FROM openjdk:17-slim
WORKDIR /app
COPY app/target/app-0.0.1-SNAPSHOT.jar /app/app-0.0.1-SNAPSHOT.jar
CMD ["java", "-jar", "app-0.0.1-SNAPSHOT.jar"]

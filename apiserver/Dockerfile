FROM maven:3.8.3-openjdk-17 AS builder
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package

FROM openjdk:20-ea-34-jdk-slim
COPY --from=builder /usr/src/app/target/apiserver-1.0.0.jar /usr/app/apiserver-1.0.0.jar

ENTRYPOINT ["java", "-jar", "/usr/app/apiserver-1.0.0.jar"]
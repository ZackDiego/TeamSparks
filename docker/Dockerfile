FROM maven:3.8.3-openjdk-17 AS build

COPY teamSpark/src /home/app/src
COPY teamSpark/pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package -Dmaven.test.skip=true

FROM openjdk:17.0.1-jdk-slim AS runtime
COPY --from=build /home/app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
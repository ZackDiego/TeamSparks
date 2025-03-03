FROM maven:3.8.6-eclipse-temurin-17 AS build

COPY teamSpark/src /home/app/src
COPY teamSpark/pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package -Dmaven.test.skip=true

FROM eclipse-temurin:17-jre AS runtime
COPY --from=build /home/app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
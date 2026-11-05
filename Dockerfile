#FROM openjdk:27-rc-jdk-slim
#COPY target/*.jar application.jar
#ENTRYPOINT ["java", "-jar", "application.jar"]

FROM maven:3.9.16-sapmachine-27 AS build
WORKDIR /
COPY /src /src
COPY pom.xml /
RUN mvn -f /pom.xml clean package

FROM openjdk:27-rc-jdk-slim
WORKDIR /
COPY /src /src
COPY --from=build /target/*.jar application.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "application.jar"]

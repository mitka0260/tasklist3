FROM openjdk:27-rc-jdk-slim
COPY target/*.jar application.jar
ENTRYPOINT ["java", "-jar", "application.jar"]

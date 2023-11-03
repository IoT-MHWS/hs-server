FROM openjdk:17-alpine

COPY "build/libs/hs-server-0.0.1-SNAPSHOT.jar" application.jar

ENTRYPOINT ["java", "-jar", "application.jar"]

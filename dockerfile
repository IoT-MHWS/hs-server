FROM gradle:8.4.0-jdk17-alpine as builder

WORKDIR /builder

COPY build.gradle gradle.properties settings.gradle ./
RUN gradle dependencies

COPY src/main src/main
RUN gradle bootJar

FROM openjdk:17-alpine

COPY --from=builder "/builder/build/libs/hs-server-0.0.1-SNAPSHOT.jar" application.jar

ENTRYPOINT ["java", "-jar", "application.jar"]

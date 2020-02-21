#BUILD
FROM maven:3.6.0-jdk-11-slim AS build
LABEL maintainer="jason.shave@microsoft.com"
LABEL source="https://github.com/jasonshave/JavaClientCredentialFlowWebAPI"
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

#PACKAGE
FROM openjdk:8
COPY --from=build /home/app/target/clientprototype-0.0.1-SNAPSHOT.jar /usr/local/lib/clientprototype.jar
ENTRYPOINT [ "java", "-jar", "/usr/local/lib/clientprototype.jar" ]

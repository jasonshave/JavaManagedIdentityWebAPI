FROM openjdk:8
LABEL maintainer="jason.shave@microsoft.com"
LABEL source="https://github.com/jasonshave/JavaClientCredentialFlowWebAPI"
VOLUME /tmp
ADD target/serverprototype.jar app.jar
EXPOSE 8080
RUN sh -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
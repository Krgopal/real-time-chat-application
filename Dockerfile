FROM openjdk:11 as build
EXPOSE 8080

VOLUME /var/log/real-time-chat-application-app

ENV work /real-time-chat-application

WORKDIR ${work}

COPY ./target/real-time-chat-application-*.jar /real-time-chat-application/real-time-chat-application.jar
COPY ./src/main/resources/docker.properties /real-time-chat-application/application.properties
ENTRYPOINT ["java","-jar","/real-time-chat-application/real-time-chat-application.jar"]

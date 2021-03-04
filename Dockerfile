FROM openjdk:8-alpine

LABEL maintainer="eduardo_gd@hotmail.com"

VOLUME /tmp

ADD target/noip*.jar noip.jar

WORKDIR /

ENTRYPOINT ["java","-jar", "/noip.jar"]

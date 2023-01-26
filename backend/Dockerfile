####################################### Build stage #######################################
FROM maven:3.8.6-openjdk-11-slim AS build-stage

ARG CITE_MAVEN_ACCOUNT_USR
ARG CITE_MAVEN_ACCOUNT_PSW

COPY pom.xml /build/
COPY sti-viewer /build/sti-viewer/
COPY sti-viewer-web /build/sti-viewer-web/
COPY settings.xml /root/.m2/settings.xml
RUN rm -f /build/sti-viewer-web/src/main/resources/config/app.env
RUN rm -f /build/sti-viewer-web/src/main/resources/config/*-dev.yml
RUN rm -f /build/sti-viewer-web/src/main/resources/logging/*.xml
RUN rm -f /build/sti-viewer-web/src/main/resources/certificates/*.crt

WORKDIR /build/

RUN mvn -Dserver.username=${CITE_MAVEN_ACCOUNT_USR} -Dserver.password=${CITE_MAVEN_ACCOUNT_PSW} dependency:go-offline 
# Build project
RUN mvn -Dserver.username=${CITE_MAVEN_ACCOUNT_USR} -Dserver.password=${CITE_MAVEN_ACCOUNT_PSW} clean package

######################################## Run Stage ########################################
FROM adoptopenjdk:11.0.8_10-jre-hotspot-bionic

COPY --from=build-stage /build/sti-viewer-web/target/sti-viewer-web.jar /app/sti-viewer-web.jar

ENTRYPOINT ["java","-Dspring.config.additional-location=file:/config/","-Dspring.profiles.active=${PROFILE}","-Djava.security.egd=file:/dev/./urandom","-jar","/app/sti-viewer-web.jar"]
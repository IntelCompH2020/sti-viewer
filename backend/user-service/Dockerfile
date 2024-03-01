####################################### Build stage #######################################
FROM maven:3.9-eclipse-temurin-17-alpine AS build-stage

ARG CITE_MAVEN_ACCOUNT_USR
ARG CITE_MAVEN_ACCOUNT_PSW

COPY pom.xml /build/
COPY user /build/user/
COPY user-web /build/user-web/
COPY settings.xml /root/.m2/settings.xml
RUN rm -f /build/user-web/src/main/resources/config/app.env
RUN rm -f /build/user-web/src/main/resources/config/*-dev.yml
RUN rm -f /build/user-web/src/main/resources/logging/*.xml
RUN rm -f /build/user-web/src/main/resources/certificates/*.crt

WORKDIR /build/

RUN mvn -Dserver.username=${CITE_MAVEN_ACCOUNT_USR} -Dserver.password=${CITE_MAVEN_ACCOUNT_PSW} dependency:go-offline 
# Build project
RUN mvn -Dserver.username=${CITE_MAVEN_ACCOUNT_USR} -Dserver.password=${CITE_MAVEN_ACCOUNT_PSW} clean package

######################################## Run Stage ########################################
FROM eclipse-temurin:17-jre-ubi9-minimal

COPY --from=build-stage /build/user-web/target/sti-viewer-user-web.jar /app/sti-viewer-user-web.jar

ENTRYPOINT ["java","-Dspring.config.additional-location=file:/config/","-Dspring.profiles.active=${PROFILE}","-Djava.security.egd=file:/dev/./urandom","-jar","/app/sti-viewer-user-web.jar"]
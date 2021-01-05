FROM gradle:jdk8 AS build
# Copy Settings, Proprerties, Build Gradle
COPY *.gradle gradle.* /appbuild/
# Define Workdir
WORKDIR /appbuild
# Download Dependencies
RUN gradle clean build --no-daemon > /dev/null 2>&1 || true
# Args to Build
ARG ENVIRONMENT
ARG SECRET
ARG DATABASE_USER
ARG DATABASE_PASSWORD
ARG DATABASE_HOST
ARG DATABASE_PORT
ARG DATABASE_SCHEMA
# Copy Src
COPY . /appbuild
# Build Project Without Test Stage
RUN gradle clean build -x test

FROM openjdk:8-jre-alpine

ENV APPLICATION_USER ktor
RUN adduser -D -g '' $APPLICATION_USER

RUN mkdir /app
RUN chown -R $APPLICATION_USER /app
USER $APPLICATION_USER

COPY --from=build /appbuild/build/libs/ktor-giphy-app-*-all.jar /app/my-application.jar
WORKDIR /app

CMD ["sh", "-c", "java -server -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:InitialRAMFraction=2 -XX:MinRAMFraction=2 -XX:MaxRAMFraction=2 -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:+UseStringDeduplication -jar my-application.jar"]
FROM maven:3.6.0-jdk-8-alpine as builder
COPY . .
# Here we skip tests to save time, because if this image is being built - tests have already passed...
RUN cd ega-data-api-res && mvn clean install -DskipTests=true -Dmaven.javadoc.skip=true -B -V -DskipDockerPush -DskipDockerBuild

FROM openjdk:8-jre-alpine
LABEL maintainer "EGA System Developers"
LABEL org.label-schema.schema-version="1.0"
LABEL org.label-schema.build-date=$BUILD_DATE
LABEL org.label-schema.vcs-url="https://github.com/EGA-archive/LocalEGA"
LABEL org.label-schema.vcs-ref=$SOURCE_COMMIT
COPY --from=builder ega-data-api-res/target/ega-data-api-res-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "ega-data-api-res-0.0.1-SNAPSHOT.jar"]

FROM navikt/java:17
LABEL maintainer="Team Bidrag" \
      email="bidrag@nav.no"

COPY ./target/bidrag-aktoerregister-*.jar app.jar

EXPOSE 8080
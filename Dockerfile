FROM navikt/java:18
LABEL maintainer="Team Bidrag" \
      email="bidrag@nav.no"

COPY ./target/bidrag-aktoerregister-*.jar app.jar

EXPOSE 8080
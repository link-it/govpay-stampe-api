FROM eclipse-temurin:11 as jre-build

# scarico e installo jdk11 e maven
RUN apt-get update -y \
&& apt-get install -y openjdk-11-jdk

ENV JAVA_HOME=/opt/java/openjdk
ENV PATH "${JAVA_HOME}/bin:${PATH}"
COPY --from=eclipse-temurin:11 $JAVA_HOME $JAVA_HOME

ARG release_tag
ENV SW_RELEASE=${release_tag}

RUN mkdir /app
COPY target/govpay-stampe-api.jar /app

ENV logging.level.root="INFO"
ENV logging.level.org.springframework="ERROR"
ENV logging.level.it.govpay="INFO"
ENV management.endpoint.health.probes.enabled=true
ENV management.health.livenessState.enabled=true
ENV management.health.readinessState.enabled=true
ENV server.servlet.context-path=/govpay-stampe-api
ENV server.port=8080


EXPOSE 8080
CMD ["java", "-jar", "/app/govpay-stampe-api.jar"]

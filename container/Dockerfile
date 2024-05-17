#FROM eclipse-temurin:11 as jre-build
# RUN $JAVA_HOME/bin/jlink \
#     --add-modules java.base \
#     --add-modules java.logging \
#     --add-modules java.sql \
#     --add-modules java.net.http \
#     --strip-debug \
#     --no-man-pages \
#     --no-header-files \
#     --compress=2 \
#     --output /javaruntime

# Define your base image
FROM rockylinux:9.1.20230215
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH "${JAVA_HOME}/bin:${PATH}"
#COPY --from=jre-build /javaruntime $JAVA_HOME
COPY --from=eclipse-temurin:11 $JAVA_HOME $JAVA_HOME

RUN yum install fontconfig -y \
&& yum install freetype -y \
&& yum update openssl -y \
&& yum update python3 -y


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
ENV server.servlet.context-path=/govpay-stampe
ENV server.port=8080


EXPOSE 8080
CMD ["java", "-jar", "/app/govpay-stampe-api.jar"]

FROM eclipse-temurin:17-jre

WORKDIR /
COPY target/ms-collector-*.jar app.jar

ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70.0"
EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar","--spring.profiles.active=docker"]

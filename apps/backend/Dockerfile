FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /workspace

COPY pom.xml .
COPY src src

RUN mvn -B -DskipTests package \
	&& find target -maxdepth 1 -type f -name '*.jar' \
		! -name '*-plain.jar' ! -name '*.original' \
		-exec cp {} target/app.jar \;

FROM eclipse-temurin:21-jre

WORKDIR /app

RUN useradd --system --create-home --uid 10001 appuser

COPY --from=build /workspace/target/app.jar app.jar

RUN chown appuser:appuser app.jar

USER appuser

EXPOSE 8080

ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["java", "-jar", "app.jar"]
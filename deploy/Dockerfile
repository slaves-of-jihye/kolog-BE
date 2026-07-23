FROM gradle:8.14.3-jdk21 AS builder

WORKDIR /workspace

COPY gradle gradle
COPY gradlew gradlew
COPY gradlew.bat gradlew.bat
COPY settings.gradle settings.gradle
COPY build.gradle build.gradle
COPY src src

RUN chmod +x ./gradlew \
    && ./gradlew clean bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre-jammy AS runtime

WORKDIR /app

RUN groupadd --system app \
    && useradd --system --gid app --create-home app \
    && mkdir -p /app/storage \
    && chown -R app:app /app

COPY --from=builder --chown=app:app /workspace/build/libs/kolog-backend-0.0.1-SNAPSHOT.jar /app/app.jar

USER app

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

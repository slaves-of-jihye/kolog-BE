FROM gradle:8.14.3-jdk21 AS builder
LABEL authors="leegyumin"

WORKDIR /workspace

COPY gradle gradle
COPY gradlew gradlew
COPY gradlew.bat gradlew.bat
COPY settings.gradle settings.gradle
COPY build.gradle build.gradle
COPY src src

RUN ./gradlew build -x test

FROM ubuntu/jre:21-24.04_stable AS runtime

WORKDIR /workspace

COPY --from=builder /workspace/build/libs/kolog-backend-0.0.1-SNAPSHOT.jar /workspace/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
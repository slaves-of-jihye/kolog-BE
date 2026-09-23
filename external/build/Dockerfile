# syntax=docker/dockerfile:1

# ---------- Build stage ----------
FROM eclipse-temurin:25-jdk-jammy AS build
WORKDIR /app

# Gradle wrapper 및 설정 파일 먼저 복사 (의존성 캐시 레이어 분리)
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon || true

# 소스 복사 후 테스트 포함 빌드
COPY src ./src
RUN ./gradlew clean build --no-daemon

# ---------- Runtime stage ----------
FROM eclipse-temurin:25-jdk-jammy AS runtime
WORKDIR /app

COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

# syntax=docker/dockerfile:1

########################################
# Stage 1: build (JDK, discarded after build)
########################################
FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /workspace

# Copy wrapper + build metadata first so the dependency-resolution layer
# only invalidates when build.gradle/settings.gradle/wrapper change,
# not on every source edit.
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew

# Warm the Gradle dependency cache in its own layer. compileJava/compileTestJava
# resolve every configuration (implementation, runtimeOnly, compileOnly,
# annotationProcessor incl. mapstruct/lombok, test*) without needing a main
# class yet, unlike bootJar/build which fail against an empty source set.
RUN ./gradlew --no-daemon compileJava compileTestJava

# Copy source and produce the boot jar. Tests are skipped here: they run in
# CI, not inside the image build, which has no network route to Redis/Postgres.
COPY src ./src
RUN ./gradlew --no-daemon bootJar -x test

########################################
# Stage 2: runtime (JRE only)
########################################
FROM eclipse-temurin:25-jre-alpine AS runtime

RUN addgroup -S spring && adduser -S -G spring spring

WORKDIR /app
COPY --from=build --chown=spring:spring /workspace/build/libs/*.jar app.jar

USER spring:spring

EXPOSE 8080

# Exec form so the JVM is PID 1 and receives SIGTERM directly (graceful shutdown).
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

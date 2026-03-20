# ─── Stage 1: build ───────────────────────────────────────────────
FROM eclipse-temurin:25-jdk-noble AS builder

WORKDIR /build

COPY build.gradle settings.gradle gradlew ./
COPY gradle/ gradle/
RUN chmod +x gradlew && ./gradlew dependencies -q --no-daemon

COPY src/ src/
RUN ./gradlew bootJar -x test -q --no-daemon

# ─── Stage 2: runtime ─────────────────────────────────────────────
FROM eclipse-temurin:25-jre-noble

RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring

WORKDIR /app

COPY --from=builder /build/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", \
  "-XX:+UseZGC", \
  "-XX:+ZGenerational", \
  "-Xms256m", "-Xmx512m", \
  "-jar", "app.jar"]
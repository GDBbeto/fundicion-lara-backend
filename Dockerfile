# -------- STAGE 1: BUILD --------
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon || true

COPY src src

RUN ./gradlew clean bootJar -x test --no-daemon


# -------- STAGE 2: RUNTIME --------
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copiamos el JAR con nombre fijo
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

# Forma shell (permite variables de entorno)
ENTRYPOINT java -jar -Dserver.port=$PORT app.jar

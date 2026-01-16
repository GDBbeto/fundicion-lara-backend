# -------- STAGE 1: BUILD --------
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

# Copiamos solo lo necesario para cachear dependencias
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon || true

# Copiamos el resto del proyecto
COPY src src

# Build del JAR
RUN ./gradlew clean bootJar -x test --no-daemon


# -------- STAGE 2: RUNTIME --------
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copiamos el JAR generado
COPY --from=build /app/build/libs/*jar app.jar

# Railway usa la variable PORT
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -jar -Dserver.port=$PORT app.jar"]

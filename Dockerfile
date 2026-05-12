# Stage 1: Build the application
FROM gradle:8.5-jdk17 AS build

WORKDIR /app

# Copiar gradle wrapper y archivos de build primero (cache de dependencias)
COPY gradlew .
COPY gradlew.bat .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Dar permisos al gradlew
RUN chmod +x gradlew

# Descargar dependencias en capa separada — se cachea si build.gradle no cambia
RUN ./gradlew dependencies --no-daemon

# Copiar el código fuente
COPY src src

# Compilar sin tests
RUN ./gradlew clean build -x test --no-daemon

# Stage 2: Imagen mínima para ejecutar el .jar
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

# Directorios para uploads y logs
RUN mkdir -p /app/uploads /app/logs

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
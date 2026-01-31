FROM gradle:8.14.3-jdk21-alpine AS build

LABEL authors="johandiaz"

WORKDIR /app
COPY . .
RUN gradle clean build -x test

#Runtime
FROM amazoncorretto:21-alpine
WORKDIR /app
COPY --from=build /app/application/app-service/build/libs/*.jar app.jar
EXPOSE 8080
# Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1
# Comando para ejecutar
ENTRYPOINT ["java", "-jar", "app.jar"]
# Étape 1 : Construction
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests

# Étape finale
FROM eclipse-temurin:21-jre-alpine

ARG USER=appuser

ARG UID=1000

ARG GID=1000

RUN addgroup -g ${GID} ${USER} && \
adduser -D -u ${UID} -G ${USER} ${USER}

WORKDIR /app

COPY --from=builder --chown=${USER}:${USER} /app/target/*.jar /app/app.jar

# COPY --chown=${USER}:${USER} entrypoint.sh /app/

# RUN chmod +x /app/entrypoint.sh && \
# chown -R ${USER}:${USER} /app

USER ${USER}

EXPOSE 9000

HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
CMD wget --spider http://localhost:9000/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
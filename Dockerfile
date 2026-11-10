#################################################################
# Stage 1: Build
#################################################################
FROM eclipse-temurin:25-jdk AS build

WORKDIR /app

# Cache do wrapper e dependencias
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Compila e gera o Fat JAR executavel
COPY src ./src
RUN ./mvnw package -DskipTests -B

#################################################################
# Stage 2: Runtime
#################################################################
FROM eclipse-temurin:25-jre

WORKDIR /app

# Cria usuario nao-root para seguranca
RUN useradd -m -u 1001 appuser

# Copia o artefato gerado na etapa anterior garantindo permissoes
COPY --from=build --chown=appuser:appuser /app/target/oficina-customer-service-*.jar app.jar

USER appuser

EXPOSE 8084

# Permite configuracao flexivel de memoria e parametros JVM
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]

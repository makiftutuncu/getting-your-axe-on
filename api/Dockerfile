FROM arm64v8/eclipse-temurin:22-jre-jammy AS builder
WORKDIR /app
COPY . .
RUN ./mvnw --batch-mode -DskipTests package

FROM arm64v8/eclipse-temurin:22-jre-jammy
LABEL maintainer="Mehmet Akif Tütüncü <m.akif.tutuncu@gmail.com>"
COPY --from=builder /app/target/*.jar /app/app.jar
ARG DB_HOST
ARG DB_PORT
ARG DB_NAME
ARG DB_USER
ARG DB_PASS
ARG DB_PARAMS
ENV DB_HOST=$DB_HOST
ENV DB_PORT=$DB_PORT
ENV DB_NAME=$DB_NAME
ENV DB_USER=$DB_USER
ENV DB_PASS=$DB_PASS
ENV DB_PARAMS=$DB_PARAMS
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

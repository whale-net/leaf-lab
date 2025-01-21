FROM sbtscala/scala-sbt:eclipse-temurin-17.0.13_11_1.10.7_3.6.2 as builder

WORKDIR /build
#COPY . .
COPY build.sbt ./
COPY project/ ./project/
COPY project/plugins.sbt ./project/plugins.sbt
COPY src ./src
RUN sbt update
RUN sbt assembly


WORKDIR /app
FROM openjdk:17-slim-buster


COPY --from=builder /build/target/scala-3.*/plant-lab-assembly-*.jar app.jar

# EXPOSE 8080
CMD ["java", "-Djdbc.drivers=org.postgresql.Driver", "-jar", "app.jar"]

FROM sbtscala/scala-sbt:eclipse-temurin-17.0.13_11_1.10.7_3.6.2 as builder

WORKDIR /build

# Copy build definition files first (less frequently changed)
COPY build.sbt ./
COPY project/ ./project/
COPY project/plugins.sbt ./project/plugins.sbt

# Run dependency resolution early to cache dependencies
RUN sbt -Dsbt.cores=4 update

# Copy source files (more frequently changed)
COPY src ./src

# Build the project
RUN sbt -Dsbt.cores=4 assembly

FROM openjdk:17-slim-buster
WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /build/target/scala-3.*/leaf-lab-assembly-*.jar app.jar

# EXPOSE 8080
CMD ["java", "-Djdbc.drivers=org.postgresql.Driver", "-jar", "app.jar"]

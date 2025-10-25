# Stage 1: Build the JAR
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy Maven wrapper and project files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src ./src

# Give execute permission to Maven wrapper (Windows sometimes messes this up)
RUN chmod +x mvnw

# Build the project
RUN ./mvnw clean package -DskipTests

# Stage 2: Create a smaller image for runtime
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/fyn-0.0.1-SNAPSHOT.jar fynApplication-v1.jar

EXPOSE 9090
ENTRYPOINT ["java", "-jar", "fynApplication-v1.jar"]

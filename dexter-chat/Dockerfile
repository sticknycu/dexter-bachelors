# Use the official OpenJDK image as a parent image
FROM openjdk:21-jdk-slim as build

# Set the working directory inside the container
WORKDIR /workspace/app

# Copy gradle executable and gradle wrapper
COPY gradlew .
COPY gradle gradle

# Copy the build file
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Copy source code
COPY src src

# Grant permissions for the gradlew to run
RUN chmod +x ./gradlew

# Build the application and skip tests
RUN ./gradlew build -x test

# Start a new stage from the JDK 21 image for a smaller final image
FROM openjdk:21-jdk-slim

WORKDIR /app

# Copy the built artifact from the previous stage
COPY --from=build /workspace/app/build/libs/*.jar app.jar

# Expose the port the app runs on
EXPOSE 9090

# Run the jar file
CMD ["java", "-jar", "app.jar"]

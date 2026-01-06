FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the pre-built jar file from GitHub Actions
COPY build/libs/*.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

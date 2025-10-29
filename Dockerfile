# Step 1: Use an official OpenJDK 17 image
FROM openjdk:17-jdk-slim

# Step 2: Set working directory in container
WORKDIR /app

# Step 3: Copy Gradle wrapper and project files
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .
COPY src ./src

# Step 4: Give execute permission to gradlew
RUN chmod +x ./gradlew

# Step 5: Build the application (produces a JAR in build/libs/)
RUN ./gradlew build -x test

# Step 6: Expose the port your Spring Boot app runs on
EXPOSE 8080

# Step 7: Run the Spring Boot JAR
CMD ["java", "-jar", "build/libs/dominAItion-backend-0.0.1-SNAPSHOT.jar"]
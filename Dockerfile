# Use the official Maven image as the base image with Java 20
FROM maven:3.9.4-amazoncorretto-20-al2023

# Set the working directory in the container
ENV GITHUB_ACCESS_TOKEN=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

# Copy the pom.xml file to the container
COPY pom.xml .

# Copy the settings.xml file to the container
COPY settings.xml .

# Download the project dependencies
RUN mvn dependency:go-offline --settings settings.xml

# Copy the application source code to the container
COPY src ./src

# Build the application
RUN mvn clean install -U --settings settings.xml

# Expose the port that the application will run on
EXPOSE 9003

# Specify the command to run the application
CMD ["java", "-jar", "target/Order-LATEST.jar"]

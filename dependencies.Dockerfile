FROM openjdk:17-jdk-alpine as base

# Set the working directory
WORKDIR /app

# Install necessary dependencies for compilation
RUN apk add --no-cache maven bash

# Copy the source code to the container
COPY . /app

# Remove unused folders
RUN rm -rf .git license .run .idea .github tools .gitattributes .gitignore .spelling_dictionary.dic docker-compose.prod.yaml docker-compose.yaml LICENSE LICENSE.md postgresql.Dockerfile README.md target src/main/resources/secrets

# Download dependencies in offline (-o) mode
RUN ./mvnw  package -Dmaven.test.skip=true

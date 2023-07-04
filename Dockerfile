# Use the UBI minimal base image for the compilation stage
FROM registry.access.redhat.com/ubi9/ubi-minimal:9.2-691 as build

# Set the working directory
WORKDIR /app

# Install necessary dependencies for compilation
RUN microdnf install --nodocs -y java-1.8.0-openjdk-headless maven && \
    microdnf clean all

# Copy the source code to the container
COPY . /app

# Apply permissions
RUN chown -R 1001:1001 /app

# Set the user to run the application
USER 1001

# Compile project
RUN mvn clean package spring-boot:repackage

# Remove secret files
RUN rm -rf /app/target/classes/secrets
# Create mountpoint for secrets
RUN mkdir -p /app/src/main/resources/secrets


# Use the UBI minimal base image for the run stage
FROM registry.access.redhat.com/ubi9/ubi-minimal:9.2-691

# Set the working directory
WORKDIR /app

# Install necessary dependencies for running
RUN microdnf install --nodocs -y java-1.8.0-openjdk-headless && \
    microdnf clean all

# Copy the directory created in the first stage into the run container
COPY --from=build /app/target /app/target

# Cambia la propiedad del directorio '/app/target' al usuario con id 1001
RUN chown 1001:1001 /app/target

# Cambia el usuario que va a ejecutar los siguientes comandos al usuario con id 1001
USER 1001

# Set the application profile in order to change the config of DB location
ENV spring_profiles_active=container

ENTRYPOINT ["java", \
    "-jar", "target/eChempad-0.0.1-SNAPSHOT.war"]

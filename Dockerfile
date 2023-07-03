# Use the UBI minimal base image
FROM registry.access.redhat.com/ubi9/ubi-minimal:9.2-691

# Set the working directory
WORKDIR /app

# Install necessary packages
RUN microdnf install --nodocs -y java-1.8.0-openjdk maven && \
    microdnf clean all

# Copy the source code to the container
COPY . /app

# Apply permissions
RUN chown -R 1001:1001 /app
# Set the user to run the application
USER 1001

# Set the application profile in order to change the config of DB location
ENV spring_profiles_active=container

RUN mvn clean compile

# Remove secret files
RUN rm -rf /app/src/main/resources/secrets
RUN rm -rf /app/target/classes/secrets
# Create mountpoint for secrets
RUN mkdir /app/src/main/resources/secrets


ENTRYPOINT ["java", \
    "-classpath", ".mvn/wrapper/maven-wrapper.jar", \
    "-Dmaven.multiModuleProjectDirectory=/app", \
    "org.apache.maven.wrapper.MavenWrapperMain", \
    "spring-boot:run"]

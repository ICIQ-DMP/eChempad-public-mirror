# Use the UBI minimal base image
FROM registry.access.redhat.com/ubi9/ubi-minimal:9.2-691

# Set the working directory
WORKDIR /app

# Install necessary packages
RUN microdnf install --nodocs -y java-1.8.0-openjdk maven && \
    microdnf clean all

# Copy the source code to the container
COPY . /app

RUN chown -R 1001:1001 /app

# Set the user to run the application
USER 1001

RUN mvn clean compile

# docker run -it -p 8081:8081 echempad:v1

ENTRYPOINT ["java", \
    "-Dspring.profiles.active=dev", \
    "-classpath", ".mvn/wrapper/maven-wrapper.jar", \
    "-Dmaven.multiModuleProjectDirectory=/app", \
    "org.apache.maven.wrapper.MavenWrapperMain", \
    "spring-boot:run"]

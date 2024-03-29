#
# eChempad is a suite of web services oriented to manage the entire
# data life-cycle of experiments and assays from Experimental
# Chemistry and related Science disciplines.
#
# Copyright (C) 2021 - present Institut Català d'Investigació Química (ICIQ)
#
# eChempad is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as
# published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program. If not, see <https://www.gnu.org/licenses/>.
#

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

# Compile project skipping testing goals (compilation, resources and run of tests)
RUN mvn clean package spring-boot:repackage -Dmaven.test.skip=true

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
RUN mkdir -p /app/target
COPY --from=build /app/target/eChempad.war /app

# Cambia la propiedad del directorio '/app/target' al usuario con id 1001
RUN chown 1001:1001 /app/eChempad.war

# Cambia el usuario que va a ejecutar los siguientes comandos al usuario con id 1001
USER 1001

# Set the application profile in order to change the config of DB location
ENV spring_profiles_active=container

ENTRYPOINT ["java", \
    "-jar", "eChempad.war"]

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

# Compilation container, creates the .jar to run the application.
# Use JDK17 alpine
FROM aleixmt/echempad-dependencies:latest AS build

# Set the working directory
WORKDIR /app

# Install necessary dependencies for compilation
RUN apk add --no-cache maven bash

# Copy the source code to the container
COPY . /app

# Remove secret files
RUN rm -rf /app/src/main/resources/secrets

# Create mountpoint for secrets
RUN mkdir -p /app/src/main/resources/secrets/eChempad_secrets

# Compile project skipping testing goals (compilation, resources and run of tests)
RUN ./mvnw package -Dmaven.test.skip=true


# Run container,
# Use JDK17 alpine
FROM openjdk:17-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the directory created in the first stage into the run container
RUN mkdir -p /app/target
COPY --from=build /app/target/eChempad.jar /app

# Copy entrypoint into image
COPY ./entrypoint.sh /app/entrypoint.sh
RUN chmod u+x /app/entrypoint.sh

# Set the application profile in order to change the config of DB location
ENV spring_profiles_active=container

ENTRYPOINT ["sh", "/app/entrypoint.sh"]
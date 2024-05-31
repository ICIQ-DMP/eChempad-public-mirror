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

# Use JDK17 alpine
FROM openjdk:17-jdk-alpine

# Set the working directory
WORKDIR /app

# Install necessary dependencies for compilation
RUN apk add --no-cache maven bash

# Copy the source code to the container
COPY . /app

# Remove secret files
RUN rm -rf /app/target/classes/secrets

# Create mountpoint for secrets
RUN mkdir -p /app/src/main/resources/secrets

# Apply permissions
RUN chown -R 1001:1001 /app

# Set the user to run the application
USER 1001

# Compile project skipping testing goals (compilation, resources and run of tests)
RUN ./mvnw clean package spring-boot:repackage -Dmaven.test.skip=true

ENTRYPOINT ["./mvnw", \
    "spring-boot:run"]

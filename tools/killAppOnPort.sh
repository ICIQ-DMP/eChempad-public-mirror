#!/usr/bin/env bash
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

if [ -z "$1" ]; then
  port="8081"
else
  port="$1"
fi
process_PID="$(lsof -ti tcp:"${port}")"
# If there is any application listening to port 8081 (which is the one used to deploy the app) kill it.
if [ -n "${process_PID}" ] &> /dev/null; then
  # if there is someone running in port 8081
  echo "process with PID ${process_PID} $(ps -p ${process_PID} -o comm=) is listening to port ${port}. Let's kill it"
  echo "${process_PID}" | xargs kill
  #sleep 5
fi
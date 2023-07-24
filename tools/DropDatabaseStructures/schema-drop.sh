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


# SCRIPT_FOLDER always has the absolute path to the running script, so all the paths that we will use are relative to
# the location of the script, and not relative to pwd, which not necessarily is the same as the location of the script.
# and not
export SCRIPT_FOLDER="$(cd "$(dirname "$(realpath "$0")")" &>/dev/null && pwd)"

# This connects to the database and sens the drop schema instructions
export PGPASSWORD='chemistry'  # To pass the parameter
psql -U amarine -h localhost -p 5432 -d eChempad -f "${SCRIPT_FOLDER}/schema-drop.sql"
#!/usr/bin/env bash

# SCRIPT_FOLDER always has the absolute path to the running script, so all the paths that we will use are relative to
# the location of the script, and not relative to pwd, which not necessarily is the same as the location of the script.
# and not
export SCRIPT_FOLDER="$(cd "$(dirname "$(realpath "$0")")" &>/dev/null && pwd)"

# This connects to the database and sens the drop schema instructions
export PGPASSWORD='chemistry'  # To pass the parameter
psql -U amarine -h localhost -p 5432 -d eChempad -f "${SCRIPT_FOLDER}/schema-drop.sql"
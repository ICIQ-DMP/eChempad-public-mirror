#!/usr/bin/env bash
if [ -z "$1" ]; then
  port="8080"
else
  port="$1"
fi

# If there is any application listening to port 8080 (which is the one used to deploy the app) kill it.
if lsof -ti tcp:"${port}" &> /dev/null; then
  # if there is someone running in port 8080
  lsof -ti tcp:"${port}" | xargs kill
fi
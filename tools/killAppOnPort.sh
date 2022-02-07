#!/usr/bin/env bash
if [ -z "$1" ]; then
  port="8081"
else
  port="$1"
fi

# If there is any application listening to port 8081 (which is the one used to deploy the app) kill it.
if lsof -ti tcp:"${port}" &> /dev/null; then
  # if there is someone running in port 8081
  PID="$(lsof -ti tcp:"${port}")"
  echo "process with PID ${PID} is listening to port ${port}. Let's kill it"
  lsof -ti tcp:"${port}" | xargs kill
  sleep 1
fi
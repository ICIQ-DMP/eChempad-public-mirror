# If there is any application listening to port 8080 (which is the one used to deploy the app) kill it.
if lsof -ti tcp:"$1"; then
  # if there is someone running in port 8080
  lsof -ti tcp:"$1" | xargs kill
fi
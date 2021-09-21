#!/usr/bin/env bash

########################################################################################################################
# * Name: export_experiment.sh
# * Description: Downloads the raw resources of an experiment pointed by its eid.
#   To do so, this script needs the API key of an user who has reading permissions from an experiment.
# * Creation Date: 29-4-21
# * Last Modified: 29-4-21
# * Author: Aleix Mariné-Tena
# * Email: amarine@iciq.es
# * Permissions: Needs to connect to the Internet and writing permissions in the folder where the script is located.
# * Dependencies: bash, curl, jq
# * Args:
#   - First argument: EID of the experiment we want to export.
#   - Second and optional argument: API key of the scratched user. If not provided, the script will look for the key in
#   a file called "key", in the same folder of this script.
# -Usage:
#
#   #################################################################################################
#   ##### DO NOT PUT THE KEY DIRECTLY IN THE TERMINAL, IT WILL BE SAVED IN THE TERMINAL HISTORY #####
#   #################################################################################################
#
#   bash export_experiment.sh EID
#   bash export_experiment.sh EID $(cat key)
#
########################################################################################################################


# First argument: EID of the experiment to export
# Second argument: key
scratch_experiment()
{
  i=$RANDOM
  local -r colon="%3A"

  # Create data folder experiment if does not exist as the place where we download abstract entities
  if [ -d experiment_${i}_ ]; then
    rm -Rf experiment_${i}_ && mkdir experiment_${i}_ && cd experiment_${i}_ || exit 7
  else
    mkdir experiment_${i}_ && cd experiment_${i}_ || exit 8
  fi

  # Perform GET of the metadata of an experiment pointed by EID
  suffix_EID=$(echo "${1}" | cut -d ":" -f2)
  curl -X GET "https://iciq.signalsnotebook.perkinelmercloud.eu/api/rest/v1.0/entities/experiment${colon}${suffix_EID}" -H "accept: application/vnd.api+json" -H "x-api-key: ${2}" > experiment_summary.json 2>/dev/null

  # Process the experiment if there is data inside the metadata json
  if [[ "[]" != "$(cat experiment_summary.json | jq .data)" ]]; then
    # Capture name of the experiment
    local entity_name="$(cat experiment_summary.json | jq .data.attributes.name | tr -d "\"")"

    # Change experiment folder name
    cd ..  # Go to entity type folder
    mv "experiment_${i}_" "experiment_${i}_${entity_name}"  # Add the name of the experiment at the end of the name of the folder
    cd "experiment_${i}_${entity_name}"  # Re-enter folder to keep process children of experiment

    if [[ ${entity_name} == "null" ]]; then
      out "Found null experiment. Aborting"
      exit 9
    fi

    out "Download experiment ${entity_name}"

    # Download metadata of all children, increasing offset 1 by 1 until data field is empty
    local j=1
    while true; do
      # Perform GET of the metadata of each children in an abstract entity identified by experiment_eid
      curl -X GET "https://iciq.signalsnotebook.perkinelmercloud.eu/api/rest/v1.0/entities/experiment${colon}${suffix_EID}/children?page[offset]=${j}&page[limit]=1" -H "accept: application/vnd.api+json" -H "x-api-key: ${2}" > "children_${j}_summary.json" 2>/dev/null

      # If there is metadata, enter the if and download the binary resource
      if [[ "[]" != "$(cat children_${j}_summary.json | jq .data)" ]]; then
        # Capture resource type
        local children_type=$(cat children_${j}_summary.json | jq .data[0].attributes.type | tr -d "\"")
        if [[ ${children_type} == "null" ]]; then
          out "Found null resource. Aborting"
          exit 10
        fi

        # Capture children experiment_eid
        local children_eid=$(cat children_${j}_summary.json | jq .data[0].id | tr -d "\"" | cut -d ":" -f2)

        out "    Downloaded resource number ${j} of type ${children_type}"

        # Download the binary resource pointed by an experiment_eid
        curl -X GET "https://iciq.signalsnotebook.perkinelmercloud.eu/api/rest/v1.0/entities/${children_type}${colon}${children_eid}/export" -H "accept: application/octet-stream" -H "x-api-key: ${key} " > "children_${j}_${children_type}" 2>/dev/null
      else
        # Remove last downloaded file, which has empty data field, so it is empty
        rm children_${j}_summary.json
        # No more files to download in this entity, escape this for and go to the next
        break
      fi
      j=$(( ${j} + 1 ))
    done
  else
    # Empty data field in entity metadata, finished downloading all entities of certain type, with last one empty
    cd ..  # Return to the root of the folder entity type (experiment, journal)
    rm -R experiment_${i}_*
    out "Experiment provided has no data. Aborting"
  fi
}

out()
{
  echo -en "\e[96m"  # Activate blue colour

  echo "${1}"

  echo -en "\e[0m"  # DeActivate colour
}

main()
{
  # Use directory where the script is located as working directory
  DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
  cd ${DIR} || exit 5

  # Obtain API key from args or file, depending on number of args
  if [ "$#" -eq 2 ]; then
    local -r key="${2}"
  elif [ "$#" -eq 1 ]; then
    local -r key="$(cat key)"
    if [[ -z "${key}" ]]; then
      out "Key not found. Supply it in the first argument or fill the file in the root of the src folder. Aborting..."
      exit 1
    fi
  else
    out "Wrong number of arguments."
    out "First supply the EID of the experiment you want to export"
    out "Second (optional) supply the API key if not defined in the file"
    out "Aborting"
    exit 2
  fi

  # Create data folder download if does not exist as the place where we download abstract entities
  if [ -d downloaded ]; then
    cd downloaded
  else
    mkdir downloaded && cd downloaded
  fi

  scratch_experiment "${1}" "${key}"

out ""
out "***********************************"
out "***      Download finished      ***"
out "***********************************"
out ""
}



dependencies=("bash" "jq" "curl")
for program in "${dependencies[@]}"; do
  which ${program} &>/dev/null
  if [[ "$?" -ne 0 ]]; then
    out "Dependency missing, install ${program} and rerun this script. Aborting..."
  fi
done

main "$@"

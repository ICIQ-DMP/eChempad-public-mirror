#!/usr/bin/env bash
#
# |===================================================================================|
# | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
# |                                                                                   |
# | This software is the property of ICIQ.                                            |
# |===================================================================================|
#

#
# |===================================================================================|
# | Copyright (C) 2021 - ${current.year} ICIQ <contact@iochem-bd.org>                            |
# |                                                                                   |
# | This software is the property of ICIQ.                                            |
# |===================================================================================|
#

#
# |===================================================================================|
# | Copyright (C) 2021 - ${project.year} ICIQ <contact@iochem-bd.org>                            |
# |                                                                                   |
# | This software is the property of ICIQ.                                            |
# |===================================================================================|
#

#
# |===================================================================================|
# | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
# |                                                                                   |
# | This software is the property of ICIQ.                                            |
# |===================================================================================|
#


########################################################################################################################
# -Name: scratch.sh
# -Description: Scratches all the data from a certain user of Signals Notebooks by using its own API key.
# -Creation Date: 26-4-21
# -Last Modified: 26-4-21
# -Author: Aleix Mariné-Tena
# -Email: amarine@iciq.es
# -Permissions: Needs to connect to the Internet and writing permissions in the folder where the script is located.
# -Dependencies: bash, curl, jq
# -Args: API key of the scratched user, via argument or via file called "key", in the same folder.
# -Usage:
#
#   #################################################################################################
#   ##### DO NOT PUT THE KEY DIRECTLY IN THE TERMINAL, IT WILL BE SAVED IN THE TERMINAL HISTORY #####
#   #################################################################################################
#
#   bash scratch.sh
#   bash scratch.sh $(cat key)
#
# -License: ?
########################################################################################################################


# First argument
scratch_experiment()
{
  i=0
  local -r colon="%3A"
  mkdir "experiment_${i}_" && cd "experiment_${i}_" || exit 8

  # Perform GET of the metadata of each experiments under a journal offset = i; n_elems = 1; include = [children, owner]
  curl -X GET "https://iciq.signalsnotebook.perkinelmercloud.eu/api/rest/v1.0/entities/experiment${colon}${1}/children?page[offset]=${i}&page[limit]=1" -H "accept: application/vnd.api+json" -H "x-api-key: ${key}" > experiment_${i}.json 2>/dev/null

  # Process the experiment if there is data inside the metadata json
  if [[ "[]" != "$(cat experiment_${i}.json | jq .data)" ]]; then
    # Capture eID of the entity that is currently being processed. At this point data field is not empty.
    local experiment_eid=$(cat experiment_${i}.json | jq .data[0].id | tr -d "\"" | cut -d ":" -f2)

    # Capture name of the experiment
    local entity_name=$(cat experiment_${i}.json | jq .data[0].attributes.name | tr -d "\"")

    # Change experiment folder name
    cd ..  # Go to entity type folder
    mv "experiment_${i}_" "experiment_${i}_${entity_name}"  # Add the name of the experiment at the end of the name of the folder
    cd "experiment_${i}_${entity_name}"  # Re-enter folder to keep processing children

    total_experiment=$(( ${total_experiment} + 1 ))

    out "    Downloading experiment number ${i} | ${entity_name}"

    # Download metadata of all children, increasing offset 1 by 1 until data field is not empty
    local j=0
    while true; do
      # Perform GET of the metadata of each children in an abstract entity identified by experiment_eid
      curl -X GET "https://iciq.signalsnotebook.perkinelmercloud.eu/api/rest/v1.0/entities/experiment${colon}${experiment_eid}/children?page[offset]=${j}&page[limit]=1" -H "accept: application/vnd.api+json" -H "x-api-key: ${key}" > "children_${j}.json" 2>/dev/null

      # If there is metadata, enter the if and download the binary resource
      if [[ "[]" != "$(cat children_${j}.json | jq .data)" ]]; then
        # Capture resource type
        local children_type=$(cat children_${j}.json | jq .data[0].attributes.type | tr -d "\"")
        # Capture children experiment_eid
        local children_eid=$(cat children_${j}.json | jq .data[0].id | tr -d "\"" | cut -d ":" -f2)

        total_resources=$(( ${total_resources} + 1 ))


        out "        Downloading experiment number ${i} | Resource number ${j} of type ${children_type}"

        # Download the binary resource pointed by an experiment_eid
        curl -X GET "https://iciq.signalsnotebook.perkinelmercloud.eu/api/rest/v1.0/entities/${children_type}${colon}${children_eid}/export" -H "accept: application/octet-stream" -H "x-api-key: ${key} " > "children_${j}_${children_type}" 2>/dev/null
      else
        # Remove last downloaded file, which has empty data field, so it is empty
        rm children_${j}.json
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
  if [ "$#" -ge 2 ]; then
    local -r key="${1}"
  else
    local -r key="$(cat key)"
    if [[ -z ${key} ]]; then
      out "Key not found. Supply it in the first argument or fill the file in the root of the src folder. Aborting..."
    fi
  fi

  # Create data folder "scratched" as the place where we download abstract entities
  rm -Rf download && mkdir download && cd download || exit 6

  # Static data
  local -r colon="%3A"

  local total_experiments=0
  local total_resources=0

  # Loop foreach journal
  local z=0
  while true; do
    # Create folder for each journal
  	mkdir "journal_${z}_" && cd "journal_${z}_" || exit 7

    # Download metadata of each journal, increasing offset 1 by 1 until var data is not empty (no more journals)
    curl -X GET "https://iciq.signalsnotebook.perkinelmercloud.eu/api/rest/v1.0/entities?page[offset]=${z}&page[limit]=1&includeTypes=journal&include=children%2C%20owner" -H "accept: application/vnd.api+json" -H "x-api-key: ${key}" > journal_${z}.json 2>/dev/null

  	# Process the journal if there is data inside the metadata json
  	if [[ "[]" != "$(cat journal_${z}.json | jq .data)" ]]; then
      # Capture eID of the journal that is currently being processed. At this point data field is not empty.
      local journal_eid=$(cat journal_${z}.json | jq .data[0].id | tr -d "\"" | cut -d ":" -f2)

  	  # Capture name of the experiment
      local journal_name=$(cat journal_${z}.json | jq .data[0].attributes.name | tr -d "\"")

  	  # Change journal folder name
  	  cd ..  # Go to downloaded type folder
      mv "journal_${z}_" "journal_${z}_${journal_name}"  # Add the name of the experiment at the end of the name of the folder
      cd "journal_${z}_${journal_name}"  # Re-enter folder to keep processing children

      out "Downloading journal number ${z} | ${journal_name}"

      local i=0
      while true; do
        # Create a folder for each experiment
        mkdir "experiment_${i}_" && cd "experiment_${i}_" || exit 8

        # Perform GET of the metadata of each experiments under a journal offset = i; n_elems = 1; include = [children, owner]
        curl -X GET "https://iciq.signalsnotebook.perkinelmercloud.eu/api/rest/v1.0/entities/journal${colon}${journal_eid}/children?page[offset]=${i}&page[limit]=1" -H "accept: application/vnd.api+json" -H "x-api-key: ${key}" > experiment_${i}.json 2>/dev/null

        # Process the experiment if there is data inside the metadata json
        if [[ "[]" != "$(cat experiment_${i}.json | jq .data)" ]]; then
          # Capture eID of the entity that is currently being processed. At this point data field is not empty.
          local experiment_eid=$(cat experiment_${i}.json | jq .data[0].id | tr -d "\"" | cut -d ":" -f2)

          # Capture name of the experiment
          local experiment_name=$(cat experiment_${i}.json | jq .data[0].attributes.name | tr -d "\"")

          # Change experiment folder name
          cd ..  # Go to journals folder
          mv "experiment_${i}_" "experiment_${i}_${experiment_name}"  # Add the name of the experiment at the end of the name of the folder
          cd "experiment_${i}_${experiment_name}"  # Re-enter folder to keep processing children

          total_experiment=$(( ${total_experiment} + 1 ))

          out "    Downloading experiment number ${i} | ${experiment_name}"

          # Download metadata of all children, increasing offset 1 by 1 until data field is not empty
          local j=0
          while true; do
            # Perform GET of the metadata of each children in an abstract experiment identified by experiment_eid
            curl -X GET "https://iciq.signalsnotebook.perkinelmercloud.eu/api/rest/v1.0/entities/experiment${colon}${experiment_eid}/children?page[offset]=${j}&page[limit]=1" -H "accept: application/vnd.api+json" -H "x-api-key: ${key}" > "children_${j}.json" 2>/dev/null

            # If there is metadata, enter the if and download the binary resource
            if [[ "[]" != "$(cat children_${j}.json | jq .data)" ]]; then
              # Capture resource type
              local children_type=$(cat children_${j}.json | jq .data[0].attributes.type | tr -d "\"")
              # Capture children experiment_eid
              local children_eid=$(cat children_${j}.json | jq .data[0].id | tr -d "\"" | cut -d ":" -f2)

              total_resources=$(( ${total_resources} + 1 ))


              out "        Downloading experiment number ${i} | Resource number ${j} of type ${children_type}"

              # Download the binary resource pointed by an experiment_eid
              curl -X GET "https://iciq.signalsnotebook.perkinelmercloud.eu/api/rest/v1.0/entities/${children_type}${colon}${children_eid}/export" -H "accept: application/octet-stream" -H "x-api-key: ${key} " > "children_${j}_${children_type}" 2>/dev/null
            else
              # Remove last downloaded file, which has empty data field, so it is empty
              rm children_${j}.json
              # No more files to download in this experiment, escape this for and go to the next
              break
            fi
            j=$(( ${j} + 1 ))
          done
        else
          # Empty data field in experiment metadata, finished downloading all entities of certain type, with last one empty
          cd ..  # Return to the root of a certain journal
          rm -R experiment_${i}_*
          break
        fi
        # Next experiment, return to journal base folder
        cd ..
        i=$(( ${i} + 1 ))
      done
  	else
      # Empty data field in journal metadata, finished downloading all entities of certain type, with last one empty
      cd ..  # Return to the root of the journal folders
      rm -R journal_${z}_*
      break
  	fi
  	# return to journal base folder
  	cd ..
    z=$(( ${z} + 1 ))
  done

out ""
out "***********************************"
out "***     Scratching finished     ***"
out "***********************************"
out ""
out "Summary of downloaded assets:"
out " - Journals: ${z} "
out " - Experiments: ${total_experiments} "
out " - Resources: ${total_resources} "
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

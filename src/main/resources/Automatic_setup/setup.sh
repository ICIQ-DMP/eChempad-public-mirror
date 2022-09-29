#!/usr/bin/env bash
#
# |===================================================================================|
# | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
# |                                                                                   |
# | This software is the property of ICIQ.                                            |
# |===================================================================================|
#
########################################################################################################################
# -Name:
# -Description:
# -Creation Date:
# -Last Modified:
# -Author:
# -Email:
# -Permissions:
# -Args:
# -Usage:
# -License:
########################################################################################################################

main()
{
  # Create the database template for eChempad
  createdb -E UTF8 --locale='en_US.utf8' -T template0 -O $(whoami) "eChempad"

  # Create the user amarine for PostGreSQL access. Give all permissions and the default password "chemistry"
  sudo -u postgres createuser --interactive --pwprompt

  # Create the file_db folder and make it accessible for the current user (which is expected to be the one running
  # eChempad)
  sudo mkdir -p /home/amarine/Desktop/eChempad/file_db
  sudo chmod 777 /home/amarine/Desktop/eChempad/file_db
  sudo chown $(whoami):$(whoami) /home/amarine/Desktop/eChempad/file_db

  # Create two hardcoded files in the DB (needed for the dummy initialization)
  echo "LICENSE" > /home/amarine/Desktop/eChempad/file_db/LICENSE.md
  echo "Photo which is text" > /home/amarine/Desktop/eChempad/file_db/3.jpg

}

set -e
main "$@"
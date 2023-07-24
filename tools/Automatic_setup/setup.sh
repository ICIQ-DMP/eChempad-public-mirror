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


# - Description: Installs the different software using the Customizer project
install_software_dependencies()
{
  customizer-install -v -o ideau
  customizer-install -v -o postman
  customizer-install -v -o pgadmin
  customizer-install -v -o psql

}

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
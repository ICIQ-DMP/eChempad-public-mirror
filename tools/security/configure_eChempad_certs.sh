#!/bin/env bash
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

# Name: eChempad certificate configurer
# Description: Script to achieve the needed state of the certificates in eChempad to communicate correctly with CAS
#   server
# Implicit arguments:
#   - ${JAVA_HOME} set to use the keytool binary. Located in ${JAVA_HOME}/bin/keytool
#   - ${ECHEMPAD_PATH} set to localize eChempad project. If not set will be set to the first existing directory:
#     - /home/amarine/Desktop/eChempad
#     - /home/amarine/Escritorio/eChempad
#     - /home/aleixmt/Desktop/eChempad
#     - /home/aleixmt/Escritorio/eChempad
#     - ${HOME}/Escritorio/eChempad
#     - ${HOME}/Desktop/eChempad
#     - /home/${SUDO_USER}/Escritorio/eChempad
#     - /home/${SUDO_USER}/Desktop/eChempad
#   - ${ECHEMPADCAS_PATH} set to localize eChempad project. If not set will be set to the first existing directory:
#     - /home/amarine/Desktop/eChempad-CAS
#     - /home/amarine/Escritorio/eChempad-CAS
#     - /home/aleixmt/Desktop/eChempad-CAS
#     - /home/aleixmt/Escritorio/eChempad-CAS
#     - ${HOME}/Escritorio/eChempad-CAS
#     - ${HOME}/Desktop/eChempad-CAS
#     - /home/${SUDO_USER}/Escritorio/eChempad-CAS
#     - /home/${SUDO_USER}/Desktop/eChempad-CAS
# Steps:
#   1.- Creates a new keystore with the eChempad certificate into ${ECHEMPAD_PATH}/src/main/resources/security/keystore
#   2.- Extracts the certificate into ${ECHEMPAD_PATH}/tools/security/eChempad.crt
#   3.- Creates a new keystore with the eChempad-CAS certificate into ${ECHEMPADCAS_PATH}/etc/cas/thekeystore
#   4.- Extracts the certificate into ${ECHEMPADCAS_PATH}/etc/cas/cas.crt
#   5.- Gets eChempad-CAS certificate from ${ECHEMPADCAS_PATH}/etc/cas/cas.crt and creates a truststore for
#       eChempad in ${ECHEMPAD_PATH}/src/main/resources/security/truststore
#   6.- Gets eChempad certificate from ${ECHEMPAD_PATH}/tools/security/eChempad.crt and creates a truststore for
#       eChempad-CAS in ${ECHEMPADCAS_PATH}/etc/cas/truststore


echempad_possible_locations=(
/home/amarine/Desktop/eChempad
/home/amarine/Escritorio/eChempad
/home/aleixmt/Desktop/eChempad
/home/aleixmt/Escritorio/eChempad
"${HOME}/Escritorio/eChempad"
"${HOME}/Desktop/eChempad"
"/home/${SUDO_USER}/Escritorio/eChempad"
"/home/${SUDO_USER}/Desktop/eChempad"
)

echempadcas_possible_locations=(
/home/amarine/Desktop/eChempad-CAS
/home/amarine/Escritorio/eChempad-CAS
/home/aleixmt/Desktop/eChempad-CAS
/home/aleixmt/Escritorio/eChempad-CAS
"${HOME}/Escritorio/eChempad-CAS"
"${HOME}/Desktop/eChempad-CAS"
"/home/${SUDO_USER}/Escritorio/eChempad-CAS"
"/home/${SUDO_USER}/Desktop/eChempad-CAS"
)

set_echempad_path()
{
  if [ -n "${ECHEMPAD_PATH}" ]; then
    echo "INFO: ECHEMPAD_PATH already set to ${ECHEMPAD_PATH}."
    return
  fi

  for location in "${echempad_possible_locations[@]}"; do
    if [ -d "${location}" ]; then
      ECHEMPAD_PATH="${location}"
      echo "INFO: ${location} found to be existing. ECHEMPAD_PATH set to ${location}"
      return
    fi
  done

  echo "ERROR: No suitable eChempad paths are found to be existing. Aborting."
  exit 2
}

set_echempadcas_path()
{
  if [ -n "${ECHEMPADCAS_PATH}" ]; then
    echo "INFO: ECHEMPADCAS_PATH already set to ${ECHEMPADCAS_PATH}."
    return
  fi

  for location in "${echempadcas_possible_locations[@]}"; do
    if [ -d "${location}" ]; then
      ECHEMPADCAS_PATH="${location}"
      echo "INFO: ${location} found to be existing. ECHEMPADCAS_PATH set to ${location}"
      return
    fi
  done

  echo "ERROR: No suitable eChempad CAS paths are found to be existing. Aborting."
  exit 3
}

# Fail on error
set -e

if [ -z "${JAVA_HOME}" ]; then
  echo "ERROR: JAVA_HOME not set. Aborting"
  exit 1
fi

# Set variables
set_echempad_path
set_echempadcas_path

# 1 Creates a new keystore with the eChempad certificate with no prompt
rm -f "${ECHEMPAD_PATH}/src/main/resources/security/keystore"
keytool -genkey -noprompt \
  -alias eChempad \
  -dname "CN=echempad.iciq.es, OU=TCC, O=ICIQ, L=Tarragona, S=Spain, C=ES" \
  -keyalg RSA \
  -validity 999 \
  -keystore "${ECHEMPAD_PATH}/src/main/resources/security/keystore" \
  -storepass changeit \
  -keypass changeit \
  -ext san=dns:echempad.iciq.es,ip:127.0.0.1

# 2 Extracts eChempad certificate from keystore
keytool -export -noprompt \
  -file "${ECHEMPAD_PATH}/tools/security/eChempad.crt" \
  -keystore "${ECHEMPAD_PATH}/src/main/resources/security/keystore" \
  -storepass changeit \
  -keypass changeit \
  -alias eChempad

# 3 Creates a new keystore with the eChempad-CAS certificate
rm -f "${ECHEMPADCAS_PATH}/etc/cas/thekeystore"
keytool -genkey -noprompt \
  -alias eChempad-CAS \
  -dname "CN=echempad-cas.iciq.es, OU=TCC, O=ICIQ, L=Tarragona, S=Spain, C=ES" \
  -keyalg RSA \
  -validity 999 \
  -keystore "${ECHEMPADCAS_PATH}/etc/cas/thekeystore" \
  -storepass changeit \
  -keypass changeit \
  -ext san=dns:echempad-cas.iciq.es,ip:127.0.0.1

# 4 Extracts eChempad CAS certificate from keystore
keytool -export -noprompt \
  -file "${ECHEMPADCAS_PATH}/etc/cas/cas.crt" \
  -keystore "${ECHEMPADCAS_PATH}/etc/cas/thekeystore" \
  -storepass changeit \
  -keypass changeit \
  -alias eChempad-CAS

# 5 Create trust store for eChempad CAS and import eChempad certificate.
rm -f "${ECHEMPADCAS_PATH}/etc/cas/truststore"
keytool -import -noprompt \
  -file "${ECHEMPAD_PATH}/tools/security/eChempad.crt" \
  -keystore "${ECHEMPADCAS_PATH}/etc/cas/truststore" \
  -storepass changeit \
  -keypass changeit \
  -alias eChempad

# 6 Create trust store for eChempad and import eChempad CAS certificate.
rm -f "${ECHEMPAD_PATH}/src/main/resources/security/truststore"
keytool -import -noprompt \
  -file "${ECHEMPADCAS_PATH}/etc/cas/cas.crt" \
  -keystore "${ECHEMPAD_PATH}/src/main/resources/security/truststore" \
  -storepass changeit \
  -keypass changeit \
  -alias eChempad-CAS

# (optional) with previous steps echempad and cas do not trust each other. Import both certs to JAVA_HOME trust store.
keytool -import -noprompt \
   -file "${ECHEMPADCAS_PATH}/etc/cas/cas.crt" \
   -keystore "${JAVA_HOME}/lib/security/cacerts" \
   -storepass changeit \
   -keypass changeit \
   -alias eChempad-CAS

keytool -import -noprompt \
   -file "${ECHEMPAD_PATH}/tools/security/eChempad.crt" \
   -keystore "${JAVA_HOME}/lib/security/cacerts" \
   -storepass changeit \
   -keypass changeit \
   -alias eChempad
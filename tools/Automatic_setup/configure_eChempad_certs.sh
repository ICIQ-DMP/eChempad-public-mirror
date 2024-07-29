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
#   1.- Creates a new keystore with the eChempad certificate into ${ECHEMPAD_PATH}/src/main/resources/secrets/keystore
#   2.- Extracts the certificate into ${ECHEMPAD_PATH}/tools/secrets/eChempad.crt
#   3.- Creates a new keystore with the eChempad-CAS certificate into ${ECHEMPADCAS_PATH}/etc/cas/thekeystore
#   4.- Extracts the certificate into ${ECHEMPADCAS_PATH}/etc/cas/cas.crt
#   5.- Gets eChempad-CAS certificate from ${ECHEMPADCAS_PATH}/etc/cas/cas.crt and creates a truststore for
#       eChempad in ${ECHEMPAD_PATH}/src/main/resources/secrets/truststore
#   6.- Gets eChempad certificate from ${ECHEMPAD_PATH}/tools/secrets/eChempad.crt and creates a truststore for
#       eChempad-CAS in ${ECHEMPADCAS_PATH}/etc/cas/truststore
# Since we are not sure how to make eChempad trust CAS, also inject the certificates into the cacerts of the JVM
#   7.- Gets eChempad-CAS certificate from ${ECHEMPADCAS_PATH}/etc/cas/cas.crt and injects it in the truststore of the
#       JVM pointed by ${JAVA_HOME}/lib/security/cacerts
#   8.- Gets eChempad certificate from ${ECHEMPAD_PATH}/tools/secrets/eChempad.crt and injects it in the truststore of
#       the truststore of the JVM pointed by ${JAVA_HOME}/lib/security/cacerts

# To work, the eChempad application only needs

declare -r mode="dev"

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

if [ "${mode}" == "dev" ]; then
  dns_echempad="echempad.iciq.es"
  dns_echempad_cas="echempad-cas"
else
  dns_echempad="echempad"
  dns_echempad_cas="echempad-cas"
fi

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
rm -f "${ECHEMPAD_PATH}/src/main/resources/secrets/keystore"
keytool -genkey -noprompt \
  -alias eChempad \
  -dname "CN=${dns_echempad}, OU=TCC, O=ICIQ, L=Tarragona, S=Spain, C=ES" \
  -keyalg RSA \
  -validity 999 \
  -keystore "${ECHEMPAD_PATH}/src/main/resources/secrets/keystore" \
  -storepass changeit \
  -keypass changeit \
  -ext san=dns:${dns_echempad},ip:127.0.0.1

# 2 Extracts eChempad certificate from keystore
keytool -export -noprompt \
  -file "${ECHEMPAD_PATH}/src/main/resources/secrets/eChempad.crt" \
  -keystore "${ECHEMPAD_PATH}/src/main/resources/secrets/keystore" \
  -storepass changeit \
  -keypass changeit \
  -alias eChempad

# 3 Creates a new keystore with the eChempad-CAS certificate
rm -f "${ECHEMPADCAS_PATH}/etc/cas/thekeystore"
keytool -genkey -noprompt \
  -alias eChempad-CAS \
  -dname "CN=${dns_echempad_cas}, OU=TCC, O=ICIQ, L=Tarragona, S=Spain, C=ES" \
  -keyalg RSA \
  -validity 999 \
  -keystore "${ECHEMPADCAS_PATH}/etc/cas/thekeystore" \
  -storepass changeit \
  -keypass changeit \
  -ext san=dns:${dns_echempad_cas},ip:127.0.0.1

# 4 Extracts eChempad CAS certificate from keystore
keytool -export -noprompt \
  -file "${ECHEMPADCAS_PATH}/etc/cas/cas.crt" \
  -keystore "${ECHEMPADCAS_PATH}/etc/cas/thekeystore" \
  -storepass changeit \
  -keypass changeit \
  -alias eChempad-CAS

# 4.1 copy cas.crt into secrets of eChempad
rm -f "${ECHEMPAD_PATH}/src/main/resources/secrets/cas.crt"
cp "${ECHEMPADCAS_PATH}/etc/cas/cas.crt" "${ECHEMPAD_PATH}/src/main/resources/secrets/"

# 4.2 copy eChempad.crt into secrets of cas
rm -f "${ECHEMPADCAS_PATH}/etc/cas/config/eChempad.crt"
cp "${ECHEMPAD_PATH}/src/main/resources/secrets/eChempad.crt" "${ECHEMPADCAS_PATH}/etc/cas/"

# 5 Create trust store for eChempad CAS and import eChempad certificate.
rm -f "${ECHEMPADCAS_PATH}/etc/cas/truststore"
keytool -import -noprompt -trustcacerts \
  -file "${ECHEMPAD_PATH}/src/main/resources/secrets/eChempad.crt" \
  -keystore "${ECHEMPADCAS_PATH}/etc/cas/truststore" \
  -storepass changeit \
  -keypass changeit \
  -alias eChempad

# 6 Create trust store for eChempad and import eChempad CAS certificate.
rm -f "${ECHEMPAD_PATH}/src/main/resources/secrets/truststore"
keytool -import -noprompt -trustcacerts \
  -file "${ECHEMPADCAS_PATH}/etc/cas/cas.crt" \
  -keystore "${ECHEMPAD_PATH}/src/main/resources/secrets/truststore" \
  -storepass changeit \
  -keypass changeit \
  -alias eChempad-CAS

# 7 Gets eChempad-CAS certificate from ${ECHEMPADCAS_PATH}/etc/cas/cas.crt and injects it in the truststore of the
# JVM pointed by ${JAVA_HOME}/lib/security/cacerts
if "${JAVA_HOME}/bin/keytool" -trustcacerts -keystore "${JAVA_HOME}/lib/security/cacerts" -storepass "changeit" -list -alias "eChempad-CAS"; then
  echo "*** - INFO: Certificate eChempad-CAS present in ${JAVA_HOME}/lib/security/cacerts, removing and reinstalling"
  "${JAVA_HOME}/bin/keytool" -delete -alias "eChempad-CAS" -keystore "${JAVA_HOME}/lib/security/cacerts" -storepass "changeit"
fi
keytool -import -noprompt -trustcacerts \
   -file "${ECHEMPADCAS_PATH}/etc/cas/cas.crt" \
   -keystore "${JAVA_HOME}/lib/security/cacerts" \
   -storepass changeit \
   -keypass changeit \
   -alias eChempad-CAS

# 8 Gets eChempad certificate from ${ECHEMPAD_PATH}/src/main/resources/secrets/eChempad.crt and injects it in the truststore of
# the truststore of the JVM pointed by ${JAVA_HOME}/lib/security/cacerts
if "${JAVA_HOME}/bin/keytool" -keystore "${JAVA_HOME}/lib/security/cacerts" -storepass "changeit" -list -alias "eChempad"; then
  echo "*** - INFO: Certificate eChempad present in ${JAVA_HOME}/lib/security/cacerts, removing and reinstalling"
  "${JAVA_HOME}/bin/keytool" -delete -alias "eChempad" -keystore "${JAVA_HOME}/lib/security/cacerts" -storepass "changeit"
fi
keytool -import -noprompt -trustcacerts \
   -file "${ECHEMPAD_PATH}/src/main/resources/secrets/eChempad.crt" \
   -keystore "${JAVA_HOME}/lib/security/cacerts" \
   -storepass changeit \
   -keypass changeit \
   -alias eChempad



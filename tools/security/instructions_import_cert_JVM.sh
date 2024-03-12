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

# Name: Certificate JVM importer
# Description: Script to import a desired certificate to the certificate store of the JVM pointed by 
# $JAVA_HOME. Uses the binary keytool located in $JAVA_HOME/bin/keytool
# Argument 1: The first argument is the path to a certificate in PEM or DER format.
# Argument 2: The second argument has to be an alias for the new installed certificate. This alias
# has to be undefined or it will show an error that it already exists.
# I used this in the past because of the error "TrustAnchorsExceptions" in order to fill the trust store of java.
# Probably you will not need it anymore, but if you see this error again, it has to be related with the trustStore being
# empty or not found.

#/bin/bash $HOME/Escritorio/eChempad/tools/CA_certificates/instructions_import_cert_JVM.sh  $HOME/Escritorio/eChempad/src/main/resources/security/eChempad.crt eChempad


if [ $# -ne 2 ]; then
  echo "*** - ERROR: This scripts needs 2 args, the path to a der or pem certificate, and the alias that will be given to this certificate. Also JAVA_HOME needs to be defined as a global variable."
  exit 2
fi

# https://connect2id.com/blog/importing-ca-root-cert-into-jvm-trust-store
# Optional to convert from PEM to DER format
if [ -n "$(echo "$1" | grep -Eo ".pem$" )" ]; then 
  openssl x509 -in "$1" -inform pem -out "$1.der" -outform der
  derfile="$1.der"
else
  derfile="$1"
fi

# Ensure that the content of the file is valid and can be parsed
"${JAVA_HOME}/bin/keytool" -v -printcert -file "${derfile}" &>/dev/null
if [ $? -ne 0 ]; then 
  echo "*** - ERROR: The certificate could not been parsed"
  exit 1
else
  echo "*** - INFO: The certificate is OK"
fi

# Delete the certificate if present
if "${JAVA_HOME}/bin/keytool" -keystore "${JAVA_HOME}/lib/security/cacerts" -storepass "changeit" -list -alias "$2" &>/dev/null; then
  echo "*** - INFO: certificate $2 already exists, deleting"
  "${JAVA_HOME}/bin/keytool" -delete -alias "$2" -keystore "${JAVA_HOME}/lib/security/cacerts" -storepass "changeit"
fi

# Import the root certificate into the JVM trust store. Default pass of the CA store is "changeit"
echo "*** - INFO: Importing certificate ${derfile} with alias $2 to ${JAVA_HOME}/lib/security/cacerts using ${JAVA_HOME}/bin/keytool"
"${JAVA_HOME}/bin/keytool" -importcert -noprompt -alias "$2" -keystore "${JAVA_HOME}/lib/security/cacerts" -storepass "changeit" -file "${derfile}" &>/dev/null
if [ $? -ne 0 ]; then
  echo "*** - INFO: Something went wrong"
  exit 0
fi

# Verify that the root certificate has been imported searching using the alias.
if ! "${JAVA_HOME}/bin/keytool" -keystore "${JAVA_HOME}/lib/security/cacerts" -storepass "changeit" -list -alias "$2" &>/dev/null; then
  echo "*** - WARNING: The certificate has not been detected in the store after the addition."
else
  echo "*** - INFO: the certificate has been installed correctly"
fi


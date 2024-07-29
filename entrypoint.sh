#!/bin/env bash

# Entrypoint of the docker container

# 7 Gets eChempad-CAS certificate from ${ECHEMPADCAS_PATH}/etc/cas/cas.crt and injects it in the truststore of the
# JVM pointed by ${JAVA_HOME}/lib/security/cacerts
if "${JAVA_HOME}/bin/keytool" -trustcacerts -keystore "${JAVA_HOME}/lib/security/cacerts" -storepass "changeit" -list -alias "eChempad-CAS"; then
  echo "*** - INFO: Certificate eChempad-CAS present in ${JAVA_HOME}/lib/security/cacerts, removing and reinstalling"
  "${JAVA_HOME}/bin/keytool" -delete -alias "eChempad-CAS" -keystore "${JAVA_HOME}/lib/security/cacerts" -storepass "changeit"
fi
keytool -import -noprompt -trustcacerts \
   -file "/app/target/classes/secrets/cas.crt" \
   -keystore "${JAVA_HOME}/lib/security/cacerts" \
   -storepass changeit \
   -keypass changeit \
   -alias eChempad-CAS

# 8 Gets eChempad certificate from ${ECHEMPAD_PATH}/tools/security/eChempad.crt and injects it in the truststore of
# the truststore of the JVM pointed by ${JAVA_HOME}/lib/security/cacerts
if "${JAVA_HOME}/bin/keytool" -keystore "${JAVA_HOME}/lib/security/cacerts" -storepass "changeit" -list -alias "eChempad"; then
  echo "*** - INFO: Certificate eChempad present in ${JAVA_HOME}/lib/security/cacerts, removing and reinstalling"
  "${JAVA_HOME}/bin/keytool" -delete -alias "eChempad" -keystore "${JAVA_HOME}/lib/security/cacerts" -storepass "changeit"
fi
keytool -import -noprompt -trustcacerts \
   -file "/app/target/classes/secrets/eChempad.crt" \
   -keystore "${JAVA_HOME}/lib/security/cacerts" \
   -storepass changeit \
   -keypass changeit \
   -alias eChempad


java -jar eChempad.jar
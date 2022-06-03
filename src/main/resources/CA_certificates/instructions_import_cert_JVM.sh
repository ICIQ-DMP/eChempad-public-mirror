# Name: Certificate JVM importer
# Description: Script to import a desired certificate to the certificate store of the JVM pointed by 
# $JAVA_HOME. Uses the binary keytool located in $JAVA_HOME/bin/keytool
# Argument 1: The first argument is the path to a certificate in PEM or DER format.
# Argument 2: The second argument has to be an alias for the new installed certificate. This alias
# has to be undefined or it will show an error that it already exists.

if [ $# -ne 2 ]; then
  echo "*** - ERROR: This scripts needs 2 args, the path to a der or pem certificate, and the alias that will begiven to this certificate. Also JAVA_HOME needs to be defined as a global variable."
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
# Import the root certificate into the JVM trust store. Default pass of the CA store is "changeit"
echo "*** - INFO: Importing certificate ${derfile} with alias $2 to ${JAVA_HOME}/jre/lib/security/cacerts using ${JAVA_HOME}/bin/keytool"
"${JAVA_HOME}/bin/keytool" -importcert -alias "$2" -keystore "${JAVA_HOME}/jre/lib/security/cacerts" -storepass "changeit" -file "${derfile}"
if [ $# -ne 0 ]; then
  echo "*** - INFO: Something went wrong"
  exit 0
fi

# Verify that the root certificate has been imported searching using the alias. 
if [ -z $("${JAVA_HOME}/bin/keytool" -keystore "${JAVA_HOME}/jre/lib/security/cacerts" -storepass "changeit" -list | grep -o "$2") ]; then
  echo "*** - WARNING: The certificate has not been detected in the store after the addition."
else
  echo "*** - INFO: the certificate has been installed correctly"
fi


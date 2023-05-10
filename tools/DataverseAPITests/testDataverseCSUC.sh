
# TOK=
URL="https://demo.dataverse.org"
DATAVERSE="ICIQ"
curl -H "X-Dataverse-key:${TOK}" -H "Content-type:application/json" -X POST "${URL}/api/dataverses/${DATAVERSE}/datasets" --upload-file "dataset-create-new-all-default-fields.json"
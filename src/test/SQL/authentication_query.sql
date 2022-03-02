
SELECT researcher.email, elementpermission.authority
FROM researcher, elementpermission
WHERE elementpermission.researcher = researcher.uuid

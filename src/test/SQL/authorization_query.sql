SELECT researcher.email, elementpermission.authority
FROM researcher, elementpermission
WHERE elementpermission.researcher = researcher.uuid 
AND
researcher.email = 'mentolado@gmail.com'
SELECT DISTINCT j.uuid, j.description, j.name
FROM journal AS j, researcher, elementpermission 
WHERE elementpermission.researcher = 
	(SELECT researcher.uuid 
	 FROM researcher 
	 WHERE researcher.email = 'elvis.not.dead@tech.es') 
AND elementpermission.journal_id = j.uuid 
AND elementpermission.authority >= 0
--
-- |===================================================================================|
-- | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
-- |                                                                                   |
-- | This software is the property of ICIQ.                                            |
-- |===================================================================================|
--

SELECT DISTINCT j.uuid, j.description, j.name
FROM journal AS j, researcher, elementpermission 
WHERE elementpermission.researcher = 
	(SELECT researcher.uuid 
	 FROM researcher 
	 WHERE researcher.email = 'elvis.not.dead@tech.es') 
AND elementpermission.journal_id = j.uuid 
AND elementpermission.authority >= 0
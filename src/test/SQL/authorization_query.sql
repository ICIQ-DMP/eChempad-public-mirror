--
-- |===================================================================================|
-- | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
-- |                                                                                   |
-- | This software is the property of ICIQ.                                            |
-- |===================================================================================|
--

SELECT researcher.email, elementpermission.authority
FROM researcher, elementpermission
WHERE elementpermission.researcher = researcher.uuid 
AND
researcher.email = 'mentolado@gmail.com'
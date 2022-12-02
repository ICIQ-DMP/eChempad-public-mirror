--
-- |===================================================================================|
-- | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
-- |                                                                                   |
-- | This software is the property of ICIQ.                                            |
-- |===================================================================================|
--

DROP TABLE IF EXISTS acl_class CASCADE;
DROP TABLE IF EXISTS acl_entry CASCADE;
DROP TABLE IF EXISTS acl_object_identity CASCADE;
DROP TABLE IF EXISTS acl_sid CASCADE;

DROP TABLE IF EXISTS authority CASCADE;
DROP TABLE IF EXISTS journal CASCADE; 
DROP TABLE IF EXISTS researcher CASCADE;  
DROP TABLE IF EXISTS securityId CASCADE;
DROP TABLE IF EXISTS experiment CASCADE;
DROP TABLE IF EXISTS document CASCADE;

DROP SEQUENCE IF EXISTS hibernate_sequence CASCADE;

DROP EXTENSION IF EXISTS plpgsql CASCADE;
DROP EXTENSION IF EXISTS "uuid-ossp" CASCADE;

DROP FUNCTION IF EXISTS uuid_generate_v1 CASCADE;
DROP FUNCTION IF EXISTS uuid_generate_v1mv CASCADE;
DROP FUNCTION IF EXISTS uuid_generate_v3 CASCADE;
DROP FUNCTION IF EXISTS uuid_generate_v4 CASCADE;
DROP FUNCTION IF EXISTS uuid_generate_v5 CASCADE;
DROP FUNCTION IF EXISTS uuid_nil CASCADE;
DROP FUNCTION IF EXISTS uuid_ns_dns CASCADE;
DROP FUNCTION IF EXISTS uuid_ns_oid CASCADE;
DROP FUNCTION IF EXISTS uuid_ns_url CASCADE;
DROP FUNCTION IF EXISTS uuid_ns_x500 CASCADE;




--
-- eChempad is a suite of web services oriented to manage the entire
-- data life-cycle of experiments and assays from Experimental
-- Chemistry and related Science disciplines.
--
-- Copyright (C) 2021 - present Institut Català d'Investigació Química (ICIQ)
--
-- eChempad is free software: you can redistribute it and/or modify
-- it under the terms of the GNU Affero General Public License as
-- published by the Free Software Foundation, either version 3 of the
-- License, or (at your option) any later version.
--
-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU Affero General Public License for more details.
--
-- You should have received a copy of the GNU Affero General Public License
-- along with this program. If not, see <https://www.gnu.org/licenses/>.
--

DROP TABLE IF EXISTS acl_class CASCADE;
DROP TABLE IF EXISTS acl_entry CASCADE;
DROP TABLE IF EXISTS acl_object_identity CASCADE;
DROP TABLE IF EXISTS acl_sid CASCADE;

DROP TABLE IF EXISTS authority CASCADE;
DROP TABLE IF EXISTS container CASCADE;
DROP TABLE IF EXISTS researcher CASCADE;  
DROP TABLE IF EXISTS securityId CASCADE;
DROP TABLE IF EXISTS document CASCADE;
DROP TABLE IF EXISTS entityimpl CASCADE;

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




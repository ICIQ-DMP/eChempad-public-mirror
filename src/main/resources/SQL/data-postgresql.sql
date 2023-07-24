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


/*
insert into acl_sid(id, principal, sid)  values
    (0, false, 'ROLE_ADMIN'),
    (1, true, 'eChempad@iciq.es')
ON CONFLICT DO NOTHING;


INSERT INTO acl_class (id, class, class_id_type) VALUES
    (0, 'org.ICIQ.eChempad.entities.genericJPAEntities.Container', 'java.util.UUID')
ON CONFLICT DO NOTHING;


INSERT INTO acl_object_identity (id, object_id_class, object_id_identity, parent_object, owner_sid, entries_inheriting) VALUES
    (0, 0, 'd3280d83-b9ad-4afe-af1e-724b1ab0da20', NULL, 1, true)
ON CONFLICT DO NOTHING;



INSERT INTO acl_entry (id, acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) VALUES
    (0, 0, 0, 1, 1, true, true, true)
ON CONFLICT DO NOTHING;

*/
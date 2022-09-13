/*
insert into acl_sid(id, principal, sid) values
    (1, true, 'admin@eChempad.es'),
    (2, false, 'ROLE_ADMIN'),
    (3, true, 'eChempad@iciq.es'),
    (4, false, 'ROLE_USER');

INSERT INTO acl_class (id, class) VALUES
    (1, 'org.ICIQ.eChempad.entities.Researcher');

INSERT INTO acl_object_identity (id, object_id_class, object_id_identity, parent_object, owner_sid, entries_inheriting) VALUES
    (1, 1, 1, NULL, 3, 0),
    (2, 1, 2, NULL, 3, 0),
    (3, 1, 3, NULL, 3, 0);

INSERT INTO acl_entry (id, acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) VALUES
    (1, 1, 1, 1, 1, 1, 1, 1),
    (2, 1, 2, 1, 2, 1, 1, 1),
    (3, 1, 3, 3, 1, 1, 1, 1),
    (4, 2, 1, 2, 1, 1, 1, 1),
    (5, 2, 2, 3, 1, 1, 1, 1),
    (6, 3, 1, 3, 1, 1, 1, 1),
    (7, 3, 2, 3, 2, 1, 1, 1);


 */
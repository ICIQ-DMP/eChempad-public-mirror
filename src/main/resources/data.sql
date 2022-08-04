insert into acl_sid(id, principal, sid) values
    (1, 1, 'admin@eChempad.es'),
    (2, 0, 'ROLE_ADMIN'),
    (3, 1, 'eChempad@iciq.es'),
    (4, 0, 'ROLE_USER');

INSERT INTO acl_class (id, class) VALUES
    (1, 'org.ICIQ.eChempad.entities.Researcher');

INSERT INTO acl_object_identity (id, object_id_class, object_id_identity, parent_object, owner_sid, entries_inheriting) VALUES
    (1, 1, 1, NULL, 3, 0),
    (2, 1, 2, NULL, 3, 0),
    (3, 1, 3, NULL, 3, 0);
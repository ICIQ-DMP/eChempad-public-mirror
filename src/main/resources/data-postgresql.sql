


/*
insert into acl_sid(id, principal, sid)  values
    (0, false, 'ROLE_ADMIN'),
    (1, true, 'eChempad@iciq.es')
ON CONFLICT DO NOTHING;


INSERT INTO acl_class (id, class, class_id_type) VALUES
    (0, 'org.ICIQ.eChempad.entities.Journal', 'java.util.UUID')
ON CONFLICT DO NOTHING;


INSERT INTO acl_object_identity (id, object_id_class, object_id_identity, parent_object, owner_sid, entries_inheriting) VALUES
    (0, 0, 'd3280d83-b9ad-4afe-af1e-724b1ab0da20', NULL, 1, true)
ON CONFLICT DO NOTHING;



INSERT INTO acl_entry (id, acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) VALUES
    (0, 0, 0, 1, 1, true, true, true)
ON CONFLICT DO NOTHING;

*/
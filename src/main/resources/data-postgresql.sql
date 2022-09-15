



insert into acl_sid(id, principal, sid)  values
    ('cd249d53-8539-4ff5-aadf-429dd21e7ef8', false, 'ROLE_ADMIN'),
    ('4d14a554-83e5-4be1-bb03-352225e6d9c3', true, 'eChempad@iciq.es')
ON CONFLICT DO NOTHING;


INSERT INTO acl_class (id, class, class_id_type) VALUES
    ('76e2725d-2403-42cc-8398-5c64912b975a', 'org.ICIQ.eChempad.entities.Journal', 'java.util.UUID')
ON CONFLICT DO NOTHING;


INSERT INTO acl_object_identity (id, object_id_class, object_id_identity, parent_object, owner_sid, entries_inheriting) VALUES
    ('d3280d83-b9ad-4afe-af1e-724b1ab0da20', '76e2725d-2403-42cc-8398-5c64912b975a', 'd3280d83-b9ad-4afe-af1e-724b1ab0da20', NULL, '4d14a554-83e5-4be1-bb03-352225e6d9c3', true)
ON CONFLICT DO NOTHING;



INSERT INTO acl_entry (id, acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) VALUES
    ('bb11dbad-0852-4dfa-8cf3-ef5b16ba72a8', 'd3280d83-b9ad-4afe-af1e-724b1ab0da20', 0, '4d14a554-83e5-4be1-bb03-352225e6d9c3', 2, true, true, true)
ON CONFLICT DO NOTHING;


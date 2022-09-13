
/*
create table researchers_authorities(
    authority_id bigserial not null primary key,
    researcher_id bigserial not null primary key,
    constraint foreign_fk_1 foreign key(authority_id)references authority(uuid),
    constraint foreign_fk_1 foreign key(researcher_id)references researcher(uuid),

    constraint unique_uk_1 unique(authority_id,researcher_id)
);*/
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";


CREATE TABLE IF NOT EXISTS acl_sid(
                        id uuid not null primary key,
                        principal boolean not null,
                        sid varchar(100) not null,
                        constraint unique_uk_1 unique(sid,principal)
);

CREATE TABLE IF NOT EXISTS acl_class(
                          id uuid not null primary key,
                          class varchar(100) not null,
                          class_id_type varchar(100),
                          constraint unique_uk_2 unique(class)
);

CREATE TABLE IF NOT EXISTS acl_object_identity(
                                    id uuid not null primary key,
                                    object_id_class uuid not null,
                                    object_id_identity varchar(255) not null,
                                    parent_object uuid default null,
                                    owner_sid uuid default null,
                                    entries_inheriting boolean not null,
                                    constraint unique_uk_3 unique(object_id_class,object_id_identity),
                                    constraint foreign_fk_1 foreign key(parent_object)references acl_object_identity(id),
                                    constraint foreign_fk_2 foreign key(object_id_class)references acl_class(id),
                                    constraint foreign_fk_3 foreign key(owner_sid)references acl_sid(id)
);

CREATE TABLE IF NOT EXISTS acl_entry(
                          id uuid not null primary key,
                          acl_object_identity uuid not null,
                          ace_order int not null,
                          sid uuid not null,
                          mask integer not null,
                          granting boolean not null,
                          audit_success boolean not null,
                          audit_failure boolean not null,
                          constraint unique_uk_4 unique(acl_object_identity,ace_order),
                          constraint foreign_fk_4 foreign key(acl_object_identity) references acl_object_identity(id),
                          constraint foreign_fk_5 foreign key(sid) references acl_sid(id)
);

/*
You will have to set the classIdentityQuery and sidIdentityQuery properties of JdbcMutableAclService to the following values, respectively:

select currval(pg_get_serial_sequence('acl_class', 'id'))
select currval(pg_get_serial_sequence('acl_sid', 'id'))
 */
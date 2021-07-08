create table application
(
    id                    bigint auto_increment
        primary key,
    creation_date_time    datetime(6) null,
    instance_manager_name varchar(512) null,
    name                  varchar(128) not null,
    visibility            int null,
    constraint unique (name)
);

create table workspace
(
    id bigint auto_increment
        primary key
);

create table filesystem_file_metadata
(
    id           bigint auto_increment primary key,
    content_type varchar(255) not null,
    deleted      bit          not null,
    file_key     varchar(255) not null,
    filename     varchar(255) not null,
    constraint unique (file_key)
);

create table sub_process
(
    process_id bigint auto_increment
        primary key
);

create table instance
(
    dtype              varchar(31)  not null,
    id                 bigint auto_increment primary key,
    instance_key       varchar(130) not null,
    status             int null,
    port               int null,
    domain_label       varchar(128) null,
    zuul_mapping       binary(255) null,
    application_id     bigint null,
    process_process_id bigint null,
    workspace_id       bigint null,
    file_metadata_id   bigint null,
    constraint unique (domain_label),
    constraint foreign key (process_process_id) references sub_process (process_id),
    constraint foreign key (file_metadata_id) references filesystem_file_metadata (id),
    constraint foreign key (application_id) references application (id),
    constraint foreign key (workspace_id) references workspace (id)
);

create table application_instances
(
    application_id bigint not null,
    instances_id   bigint not null,
    primary key (application_id, instances_id),
    constraint unique (instances_id),
    constraint foreign key (application_id) references application (id),
    constraint foreign key (instances_id) references instance (id)
);

create table spring_application
(
    secret varchar(255) not null,
    id     bigint       not null primary key,
    constraint foreign key (id) references application (id)
);

create table available_spring_instance
(
    id                 bigint auto_increment primary key,
    creation_time      datetime(6) null,
    deleted            bit null,
    instance_key       varchar(130) not null,
    last_update        datetime(6) null,
    actual_instance_id bigint null,
    application_id     bigint null,
    artifact_id        bigint null,
    constraint foreign key (actual_instance_id) references instance (id),
    constraint foreign key (artifact_id) references filesystem_file_metadata (id),
    constraint foreign key (application_id) references spring_application (id)
);

create table environment_variable
(
    id                         bigint auto_increment primary key,
    environment_variable_name  varchar(255) null,
    environment_variable_value varchar(255) null
);

create table instance_environment
(
    spring_instance_id bigint not null,
    environment_id     bigint not null,
    primary key (spring_instance_id, environment_id),
    constraint unique (environment_id),
    constraint foreign key (environment_id) references environment_variable (id),
    constraint foreign key (spring_instance_id) references instance (id)
);

create table spring_application_default_environment
(
    spring_application_id  bigint not null,
    default_environment_id bigint not null,
    primary key (spring_application_id, default_environment_id),
    constraint unique (default_environment_id),
    constraint foreign key (spring_application_id) references spring_application (id),
    constraint foreign key (default_environment_id) references environment_variable (id)
);

create table static_file_application
(
    secret varchar(255) not null,
    id     bigint       not null primary key,
    constraint foreign key (id) references application (id)
);
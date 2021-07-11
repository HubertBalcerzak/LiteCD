create table application_with_environment
(
    id bigint not null primary key,
    constraint foreign key (id) references application (id)
);

insert into application_with_environment (id)
        (select id from spring_application);

rename table spring_application_default_environment to application_with_environment_default_environment;

alter table application_with_environment_default_environment
    rename column spring_application_id to application_with_environment_id;

alter table application_with_environment_default_environment
    drop foreign key application_with_environment_default_environment_ibfk_1;

alter table application_with_environment_default_environment
    add constraint fk_application_with_env_default_env__application_with_env foreign key (application_with_environment_id) references application_with_environment (id);


create table docker_application
(
    image_name        varchar(255),
    registry_password varchar(128),
    registry_username varchar(128),
    secret            varchar(255) not null,
    id                bigint       not null primary key,
    constraint fk_docker_application__id__application__id
        foreign key (id) references application (id)
);

alter table instance_environment
    rename column spring_instance_id to instance_with_environment_id;

create table available_docker_instance
(
    id                 bigint auto_increment primary key,
    creation_time      datetime(6) null,
    deleted            bit null,
    instance_key       varchar(130) not null,
    last_update        datetime(6) null,
    actual_instance_id bigint null,
    application_id     bigint null,
    constraint foreign key (actual_instance_id) references instance (id),
    constraint foreign key (application_id) references docker_application (id)
)
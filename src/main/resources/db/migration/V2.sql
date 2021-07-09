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
    id                bigint not null primary key,
    constraint fk_docker_application__id__application__id
        foreign key (id) references application (id)
);

alter table instance_environment
    rename column spring_instance_id to instance_with_environment_id;
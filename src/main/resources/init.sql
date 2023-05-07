create table manager
(
    id   uuid         not null
        primary key,
    name varchar(255) not null
);

create table programmer
(
    id          uuid         not null
        primary key,
    name        varchar(255) not null,
    skill_level varchar(255) not null,
    type        varchar(255) not null
);

create table project
(
    id   uuid         not null
        primary key,
    name varchar(255) not null
);


create table manager_project
(
    manager_id uuid not null
        constraint fk_manager_id
            references manager,
    project_id uuid not null
        constraint fk_project_id
            references project,
    primary key (manager_id, project_id)
);

create table programmer_project
(
    programmer_id uuid not null
        constraint fk_programmer_id
            references programmer,
    project_id    uuid not null
        constraint fk_project_id
            references project,
    primary key (programmer_id, project_id)
);



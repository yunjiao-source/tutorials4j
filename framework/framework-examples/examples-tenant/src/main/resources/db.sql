create table public.t_user_tenant
(
    id         bigint       not null
        primary key,
    tenant_id  varchar(255) not null,
    age        integer,
    email      varchar(255),
    name       varchar(255),
    password   varchar(255),
    secret_key varchar(255)
);

alter table public.t_user_tenant
    owner to postgres;

create table public.t_user
(
    id                 bigint       not null
        primary key,
    age                integer,
    created_date       timestamp(6),
    email              varchar(255) not null,
    last_modified_date timestamp(6),
    name               varchar(255) not null,
    password           varchar(255) not null,
    secret_key         varchar(255),
    sex                varchar(255)
);

alter table public.t_user
    owner to postgres;


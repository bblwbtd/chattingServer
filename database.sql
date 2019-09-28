create table users
(
    username varchar(64)  not null
        constraint users_pk
            primary key,
    password varchar(128) not null
);

alter table users
    owner to postgres;

create table record
(
    id      serial                  not null
        constraint record_pk
            primary key,
    "from"  varchar(64)             not null
        constraint record_users_username_fk
            references users
            on update cascade on delete cascade,
    "to"    varchar(64)             not null
        constraint record_users_username_fk_2
            references users
            on update cascade on delete cascade,
    content text                    not null,
    time    timestamp default now() not null
);

alter table record
    owner to postgres;

create index record_from_index
    on record ("from");

create index record_to_index
    on record ("to");

create table friends
(
    id     serial            not null
        constraint friends_pk
            primary key,
    user1  varchar(64)       not null
        constraint friends_users_username_fk
            references users
            on update cascade on delete cascade,
    user2  varchar(64)       not null
        constraint friends_users_username_fk_2
            references users
            on update cascade on delete cascade,
    status integer default 0 not null
);

alter table friends
    owner to postgres;

create unique index friends_user1_user2_uindex
    on friends (user1, user2);



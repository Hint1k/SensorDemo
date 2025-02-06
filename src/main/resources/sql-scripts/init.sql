/* PostgreSQL script */

\connect postgres

drop database if exists sensor with (force);
create database sensor;

\connect sensor

drop schema if exists sensor;
create schema sensor;

set search_path = sensor, public;

drop table if exists sensor;
create table sensor
(
    id   bigserial primary key,
    name varchar(55) not null,
    constraint name_unique unique (name)
);

drop table if exists measurement;
create table measurement
(
    id          bigserial primary key,
    temperature double precision not null,
    rain        boolean          not null,
    sensor_id   bigserial        not null,
    foreign key (sensor_id) references sensor (id)
);

drop table if exists public.users;
create table public.users
(
    id       bigserial primary key,
    username varchar(45) not null,
    password varchar(68) not null,
    enabled  integer     not null
);

drop table if exists public.authorities;
create table public.authorities
(
    id        bigserial primary key,
    username  varchar(45) not null,
    authority varchar(45) not null,
    user_id   bigserial   not null,
    foreign key (user_id) references users (id)
);

insert into public.users (id, username, password, enabled)
values (1, 'manager', '$2a$10$U.TJCuMA4c6lka5Xq7i43OK9iDoA1/niZU3Gi6Xez1JzB7wNwvQzu', 1),
       (2, 'admin', '$2a$10$U.TJCuMA4c6lka5Xq7i43OK9iDoA1/niZU3Gi6Xez1JzB7wNwvQzu', 1);

insert into public.authorities (id, username, authority, user_id)
values (1, 'manager', 'ROLE_MANAGER', 1),
       (2, 'admin', 'ROLE_ADMIN', 2);
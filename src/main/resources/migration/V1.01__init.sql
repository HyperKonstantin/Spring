create table departments (
    id bigint not null,
    name varchar(255),
    primary key (id)
);

create table users (
    id bigint not null,
    age integer not null,
    name varchar(255),
    phone varchar(255),
    department bigint,
    primary key (id)
);

alter table if exists users
add constraint FKlnexc2tom4xayojrgw90wiq2x
foreign key (department) references departments;
drop table departments cascade;
drop table users;

create table departments (
    department_id bigint not null,
    department varchar(255),
    primary key (department_id)
);

create table users (
    id integer not null,
    age integer not null,
    name varchar(255),
    phone varchar(255),
    department bigint,
    primary key (id)
);

alter table if exists users
add constraint FKlnexc2tom4xayojrgw90wiq2x
foreign key (department) references departments on delete cascade;

insert into departments values (1, 'development');
insert into departments values (2, 'debug');
insert into departments values (3, 'testing');

insert into users values (1, 19, 'Kostya', '+375291859006', 1);
insert into users values (2, 20, 'Anton', '+375441987101', 3);

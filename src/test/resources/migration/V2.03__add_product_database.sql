create table products (
    id bigint,
    name varchar(50),
    price integer,
    primary key(id)
);

insert into products(id, name, price) values(1, 'apple', 100);
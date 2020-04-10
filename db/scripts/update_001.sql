create table if not exists post (
   id serial primary key not null,
   name varchar(200),
   text text,
   link varchar(200)
);

create table if not exists log (
   id serial primary key not null,
   date timestamp
);
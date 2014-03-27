create table person(id INT PRIMARY KEY, name VARCHAR(255), age INT);

insert into person (id, name, age) values (1, 'Doe', 25);
insert into person (id, name, age) values (2, 'Dupont', 34);

create table birth(id INT PRIMARY KEY, name VARCHAR(255), birthdate DATE, person_id INT);

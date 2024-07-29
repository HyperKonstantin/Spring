create view users_view as
select users.id, users.name, age, salary, departments.name as department from users
join departments on users.department = departments.id
CREATE TABLE groups(
id serial PRIMARY KEY,
name VARCHAR(5) NOT NULL
);

CREATE TABLE students(
id serial PRIMARY KEY,
group_id int REFERENCES groups(id),
first_name VARCHAR(50) NOT NULL,
last_name VARCHAR(50) NOT NULL
);

CREATE TABLE courses(
id serial PRIMARY KEY,
name VARCHAR(50) NOT NULL,
description text NOT NULL
);

CREATE TABLE students_courses(
student_id int REFERENCES students(id) ON UPDATE CASCADE ON DELETE CASCADE,
course_id int REFERENCES courses(id) ON UPDATE CASCADE ON DELETE CASCADE,
CONSTRAINT student_course UNIQUE (student_id, course_id)
);
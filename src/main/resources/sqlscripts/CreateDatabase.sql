CREATE DATABASE school;

CREATE USER school_admin WITH PASSWORD 'admin';
GRANT ALL ON DATABASE school TO school_admin;
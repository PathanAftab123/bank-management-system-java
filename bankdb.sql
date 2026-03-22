CREATE DATABASE bankdb;

USE bankdb;

CREATE TABLE accounts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50),
    balance DOUBLE
);
select * from accounts;
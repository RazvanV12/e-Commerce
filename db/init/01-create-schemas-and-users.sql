CREATE DATABASE IF NOT EXISTS ecommerce;

CREATE DATABASE IF NOT EXISTS ecommerce_auth;


CREATE USER IF NOT EXISTS 'ecommerce_user'@'%' IDENTIFIED BY 'ecommerce_pass';


GRANT ALL PRIVILEGES ON ecommerce.* TO 'ecommerce_user'@'%';
GRANT ALL PRIVILEGES ON ecommerce_auth.* TO 'ecommerce_user'@'%';

FLUSH
PRIVILEGES;

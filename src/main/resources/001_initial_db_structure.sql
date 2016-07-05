DROP TABLE IF EXISTS users;
CREATE TABLE users(
  id INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  registration_time TIMESTAMP,
  first_name VARCHAR(255),
  last_name VARCHAR(255),
  birth_date DATE
);

DROP TABLE IF EXISTS posts;
DROP TABLE IF EXISTS post_privacy_types;

CREATE TABLE post_privacy_types(
id INT PRIMARY KEY,
name VARCHAR(255)
);

INSERT INTO post_privacy_types(id,name) VALUES
(0,'default'),
(1, 'public'),
(2, 'private'),
(3, 'protected');

CREATE TABLE posts(
  id INT PRIMARY KEY AUTO_INCREMENT,
  author INT NOT NULL REFERENCES users(id),
  creation_date TIMESTAMP,
  text TEXT,
  post_privacy_type INT DEFAULT 0 REFERENCES post_privacy_types(id)
);
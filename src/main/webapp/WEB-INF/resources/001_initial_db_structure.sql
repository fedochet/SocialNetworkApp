DROP VIEW IF EXISTS post_views;
DROP TABLE IF EXISTS likes;
DROP TABLE IF EXISTS post_comments;
DROP TABLE IF EXISTS user_followers;
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS posts;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS post_types;
DROP TABLE IF EXISTS user_roles;

CREATE TABLE user_roles (
  id INT PRIMARY KEY,
  name VARCHAR(255)
);

INSERT INTO user_roles VALUES
  (0, 'user'),
  (1, 'admin');

CREATE TABLE post_types(
  id INT PRIMARY KEY,
  name VARCHAR(255)
);

INSERT INTO post_types(id,name) VALUES
  (0,'default'),
  (1, 'public'),
  (2, 'private'),
  (3, 'protected');

CREATE TABLE users(
  id INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  registration_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  first_name VARCHAR(255),
  last_name VARCHAR(255),
  info VARCHAR(255),
  birth_date DATE,
  role INT NOT NULL REFERENCES user_roles(id)
);

CREATE TABLE posts(
  id INT PRIMARY KEY AUTO_INCREMENT,
  author_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  creation_time TIMESTAMP,
  text TEXT,
  post_privacy_type INT DEFAULT 0 REFERENCES post_types(id)
);

CREATE TABLE likes(
  id INT PRIMARY KEY AUTO_INCREMENT,
  post_id INT NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
  user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  creation_time TIMESTAMP
);

CREATE TABLE post_comments(
  id INT PRIMARY KEY AUTO_INCREMENT,
  post_id INT NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
  author_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  creation_time TIMESTAMP
);


CREATE TABLE user_followers(
  id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  follower_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  UNIQUE (user_id, follower_id)
);

CREATE VIEW post_views(
    post_id,
    post_text,
    post_creation_time,
    author_id,
    author_username,
    author_firstname,
    author_lastname,
    post_likes) AS
  SELECT posts.id,
    posts.text,
    posts.creation_time,
    posts.author_id,
    users.username,
    users.first_name,
    users.last_name,
    count(likes.id)
  FROM posts
    JOIN users ON posts.author_id=users.id
    LEFT JOIN likes ON posts.id=likes.post_id
  GROUP BY posts.id;
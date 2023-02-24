CREATE TABLE IF NOT EXISTS post_schema.post
(
  id SERIAL PRIMARY KEY,
  title text,
  link text UNIQUE,
  description text,
  created timestamp
);
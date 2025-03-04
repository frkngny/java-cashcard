CREATE TABLE IF NOT EXISTS CASHCARD (
   id SERIAL,
   amount double precision not null default 0,
   owner VARCHAR(256) NOT NULL,
   PRIMARY KEY (id)
);
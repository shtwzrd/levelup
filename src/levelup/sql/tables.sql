-- name: create-goals-table-if-not-exists!
-- create the goals table if it does not exist
CREATE TABLE IF NOT EXISTS goals (
   id                 SERIAL PRIMARY KEY NOT NULL
   , templateid       CITEXT
   , parentid         CITEXT
   , ownerid          TEXT               NOT NULL
   , title            TEXT               NOT NULL
   , category         TEXT               NOT NULL
   , difficulty       TEXT               NOT NULL
   , description      TEXT
   , reason           TEXT
   , isrecurring      BOOLEAN            NOT NULL
   , ispublic         BOOLEAN            NOT NULL
   , iscompleted      BOOLEAN            NOT NULL
   , seq              INT(2)
   , startdate        TIMESTAMP
   , enddate          TIMESTAMP
   , completiondate   TIMESTAMP
);

-- name: drop-goal-table!
-- drop the goal table
DROP TABLE goals;

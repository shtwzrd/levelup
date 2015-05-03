-- name: create-goals-table-if-not-exists!
-- create the goals table if it does not exist
CREATE TABLE IF NOT EXISTS goals (
   id                 SERIAL PRIMARY KEY NOT NULL
   , templateid       INT
   , parentid         INT
   , ownerid          INT                NOT NULL
   , title            TEXT               NOT NULL
   , flow             INT                NOT NULL
   , startdate        TIMESTAMP          NOT NULL
   , enddate          TIMESTAMP          NOT NULL
   , completiondate   TIMESTAMP
   , category         TEXT               NOT NULL
   , difficulty       TEXT               NOT NULL
   , description      TEXT
   , reason           TEXT
   , isrecurring      BOOLEAN            NOT NULL
   , ispublic         BOOLEAN            NOT NULL
   , iscompleted      BOOLEAN            NOT NULL
);

-- name: drop-goal-table!
-- drop the goal table
DROP TABLE goals;

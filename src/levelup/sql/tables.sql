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

-- name: create-users-table-if-not-exists!
-- create the users table if it does not exist

CREATE TABLE IF NOT EXISTS users (
   id                 SERIAL PRIMARY KEY NOT NULL
   , email            VARCHAR(250)       NOT NULL
   , social           INT                NOT NULL
   , health           INT                NOT NULL
   , happiness        INT                NOT NULL
   , finance          INT                NOT NULL
   , knowledge        INT                NOT NULL
   , spirit           INT                NOT NULL
   , xp               INT                NOT NULL
   , registrationdate TIMESTAMP          NOT NULL
   , displayname      VARCHAR(250)
   , credentialkey    INT                NOT NULL
);


-- name: drop-goals-table!
-- drop the goals table
DROP TABLE goals;

-- name: drop-users-table!
-- drop the users table
DROP TABLE users;

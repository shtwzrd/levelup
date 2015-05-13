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
   , email            VARCHAR(254)       NOT NULL UNIQUE
   , social           INT                DEFAULT 0
   , health           INT                DEFAULT 0
   , happiness        INT                DEFAULT 0
   , finance          INT                DEFAULT 0
   , knowledge        INT                DEFAULT 0
   , spirit           INT                DEFAULT 0
   , registrationdate TIMESTAMP          DEFAULT CURRENT_TIMESTAMP
   , displayname      VARCHAR(254)
);

CREATE TABLE IF NOT EXISTS basic_secrets (
    id                SERIAL PRIMARY KEY NOT NULL
   , userid           INT                NOT NULL
   , secret           TEXT               NOT NULL
)

-- name: drop-goals-table!
-- drop the goals table
DROP TABLE goals;

-- name: drop-users-table!
-- drop the users table
DROP TABLE users;

-- name: drop-basic-secrets-table!
-- drop the basic_secrets table
DROP TABLE basic_secrets;



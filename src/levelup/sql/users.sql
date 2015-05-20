-- name: insert-user-with-basic-secret<!
-- creates a single user with a basic password for authentication
INSERT INTO users (
       email
       , displayname
       , basic_secret
)
VALUES (
       :email
       , :displayname
       , :basic_secret
);

-- name: get-all-users
-- Gets all non-sensitive data from users
SELECT id
       , email
       , displayname
       , social
       , health
       , happiness
       , finance
       , knowledge
       , spirit
FROM users;

-- name: get-user-by-email
-- Gets all data for a user specified by email address
SELECT email
       , social
       , health
       , happiness
       , finance
       , knowledge
       , spirit
       , displayname
       , id
FROM users
WHERE email = :email;

-- name: get-user-by-id
-- Gets all data for a user specified by user id
SELECT email
       , social
       , health
       , happiness
       , finance
       , knowledge
       , spirit
       , displayname
       , id
FROM users
WHERE id = :id;

-- name: get-user-secrets
-- Returns all the encrypted secrets for the user
SELECT id
       , basic_secret
FROM users
WHERE id = :id;

-- name: get-user-secrets-by-email
-- Returns all the encrypted secrets for the user, with email as the identifier
SELECT email
       , basic_secret
FROM users
WHERE email = :email;


-- name: update-user-stats-by-id!
-- updates a single user's stats
UPDATE users
SET social      = :social
  , health      = :health
  , happiness   = :happiness
  , finance     = :finance
  , knowledge   = :knowledge
  , spirit      = :spirit
WHERE id = :id;

-- name: update-user-by-id!
-- updates a single user's email and/or displayname
UPDATE users
SET email       = :email
  , displayname = :displayname
WHERE id = :id;

-- name: delete-user!
-- removes a single user by id
DELETE FROM users
WHERE id = :id;

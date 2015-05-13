-- name: insert-user<!
-- creates a single user
INSERT INTO users (
       email
       , displayname
)
VALUES (
       :email
       , :displayname
);

-- name: get-all-users
-- Gets all data, including stats and email addresses, for all users
SELECT * FROM users;

-- name: get-user-by-email
-- Gets all data for a user specified by email address
SELECT * FROM users
WHERE email = :email;

-- name: get-user-by-id
-- Gets all data for a user specified by user id
SELECT * FROM users
WHERE id = :id;

-- name: update-user-by-id!
-- updates a single user's data
UPDATE users
SET email       = :email
  , social      = :social
  , health      = :health
  , happiness   = :happiness
  , finance     = :finance
  , knowledge   = :knowledge
  , spirit      = :spirit
  , displayname = :displayname
WHERE id = :id;

-- name: delete-user!
-- removes a single user by id
DELETE FROM users
WHERE id = :id;

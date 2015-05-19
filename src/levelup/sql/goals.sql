-- name: get-all-goal-templates
-- Selects all publically listed top-level goals
SELECT * FROM goals
WHERE ispublic = true
      AND parentid = 0;

-- name: get-all-user-goals
-- Selects all private goals owned by ownerid
SELECT * FROM goals
WHERE ispublic = false
      AND ownerid = :ownerid;

-- name: insert-goal<!
-- inserts a single goal
INSERT INTO goals (
    templateid
  , parentid
  , ownerid
  , title
  , flow
  , startdate
  , enddate
  , completiondate
  , category
  , difficulty
  , description
  , reason
  , isrecurring
  , ispublic
  , iscompleted )
VALUES (
    :templateid
  , :parentid
  , :ownerid
  , :title
  , :flow
  , :startdate
  , :enddate
  , :completiondate
  , :category
  , :difficulty
  , :description
  , :reason
  , :isrecurring
  , :ispublic
  , :iscompleted );

-- name: update-goal!
-- updates a single goal
UPDATE goals
SET templateid     = :templateid
  , parentid       = :parentid
  , title          = :title
  , flow           = :flow
  , startdate      = :startdate
  , enddate        = :enddate
  , completiondate = :completiondate
  , category       = :category
  , difficulty     = :difficulty
  , description    = :description
  , reason         = :reason
  , isrecurring    = :isrecurring
  , ispublic       = :ispublic
  , iscompleted    = :iscompleted
WHERE id = :id;

-- name: delete-goal!
-- removes a single goal by id
DELETE FROM goals
WHERE id = :id;

-- name: get-goal
-- gets a single goal by id
SELECT * FROM goals
WHERE id = :id;

-- name: get-goal-owner
-- returns the uid of the user who owns the goal
SELECT ownerid FROM goals
WHERE id = :id;

-- name: inc-goal-followers!
-- +1 to the follower count of the goal matching the provided id
UPDATE goals
       SET followers = followers + 1
       WHERE id = :id

-- name: dec-goal-followers!
-- -1 to the follower count of the goal matching the provided id
UPDATE goals
       SET followers = followers - 1
       WHERE id = :id

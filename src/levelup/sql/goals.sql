-- name: get-all-goal-templates
-- Selects all publically listed top-level goals
SELECT id
       , templateid
       , parentid
       , ownerid
       , title
       , category
       , difficulty
       , description
       , reason
       , isrecurring
       , ispublic
       , flow
       , startdate
       , enddate
       , iscompleted
FROM   goals
WHERE ispublic = true
      AND parentid = 0;

-- name: get-all-user-goals
-- Selects all private goals owned by ownerid
SELECT * FROM  goals
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

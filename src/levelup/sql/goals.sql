-- name: all-goal-templates
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
FROM   goals
WHERE ispublic = true
      AND parentid = 0;

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

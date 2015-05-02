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

(ns levelup.data
  (:require [environ.core :refer [env]]
            [yesql.core   :refer [defqueries]]))

(def db-connection {:connection (env :database-url)})

(defqueries "levelup/sql/goals.sql")

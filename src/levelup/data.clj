(ns levelup.data
  (:require [environ.core :refer [env]]
            [clojure.string :refer [split]]
            [clojure.java.jdbc :as jdbc]
            [yesql.core :refer [defqueries]]))


(def db-connection
  (env :database-url))

(defqueries "levelup/sql/goals.sql")
(defqueries "levelup/sql/tables.sql")

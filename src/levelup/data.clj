(ns levelup.data
  (:require [environ.core :refer [env]]
            [clojure.string :refer [split]]
            [clojure.java.jdbc :as jdbc]
            [yesql.core :refer [defqueries]]))

(defn update-values [map seq fn]
  (reduce #(update-in % [%2] fn) map seq))

(defn cull-nils [map]
  (into {} (remove #(nil? (val %)) map)))

(def db-connection
  (env :database-url))

(defqueries "levelup/sql/goals.sql")
(defqueries "levelup/sql/tables.sql")

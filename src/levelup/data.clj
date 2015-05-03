(ns levelup.data
  (:require [environ.core :refer [env]]
            [clojure.string :refer [split]]
            [clojure.java.jdbc :as jdbc]
            [clj-time.coerce :as c]
            [yesql.core :refer [defqueries]]))

(defn update-values [map seq fn]
  (reduce #(update-in % [%2] fn) map seq))

(defn cull-nils [map]
  (into {} (remove #(nil? (val %)) map)))

(defn coerce-timestamps [map]
  (cull-nils (update-values map [:startdate :enddate :completiondate] c/from-sql-time)))

(def db-connection
  (env :database-url))

(defqueries "levelup/sql/goals.sql")
(defqueries "levelup/sql/tables.sql")

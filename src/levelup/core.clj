(ns levelup.core
  (:require [levelup.handler :refer [app]]
            [levelup.data :as data-access]
            [clojure.java.jdbc :as jdbc]
            [ring.adapter.jetty :refer [run-jetty]])
  (:gen-class))

(defn init[]
  (jdbc/with-db-transaction [connection data-access/db-connection]
    (data-access/create-goals-table-if-not-exists! connection)))

(defn parse-port [port]
  (Integer/parseInt (or port (System/getenv "PORT") "3000")))

(defn -main [& [port]]
  (let [port (parse-port port)]
    (init)
    (run-jetty app {:port port :join? false})))

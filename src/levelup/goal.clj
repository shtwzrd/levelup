(ns levelup.goal
  (:require [schema.core :as s]
            [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [clj-time.format :as f]
            [clj-time.coerce :as c]
            [levelup.data :as db]
            [ring.swagger.schema :as rs])
  (:import [java.sql Timestamp]))

(s/defschema Goal {:id Long
                   (s/optional-key :templateid) Long
                   (s/optional-key :parentid) Long
                   :ownerid Long
                   :title String
                   :flow Long
                   :startdate org.joda.time.DateTime
                   :enddate org.joda.time.DateTime
                   (s/optional-key :completiondate) org.joda.time.DateTime
                   :category (s/enum :health :spirit :knowledge :finance :happiness :social)
                   :difficulty (s/enum :trivial :simple :average :huge :colossal)
                   (s/optional-key :description) String
                   (s/optional-key :reason) String
                   :isrecurring Boolean
                   :ispublic Boolean
                   :iscompleted Boolean})

(s/defschema NewGoal (dissoc Goal :id))

;; Domain funcs

(defn get-goal [id]
  (map db/coerce-timestamps
       (db/get-goal db/db-connection id)))

(defn get-goals []
  (map db/coerce-timestamps
       (db/get-all-goal-templates db/db-connection)))

(defn delete! [id]
  (db/delete-goal! db/db-connection id))

(defn add! [new-goal]
  (let [goal (db/insert-goal<! db/db-connection
                               (:templateid new-goal)
                               (:parentid new-goal)
                               (:ownerid new-goal)
                               (:title new-goal)
                               (:flow new-goal)
                               (c/to-sql-time (:startdate new-goal))
                               (c/to-sql-time (:enddate new-goal))
                               (c/to-sql-time (:completiondate new-goal))
                               (name (:category new-goal))
                               (name (:difficulty new-goal))
                               (:description new-goal)
                               (:reason new-goal)
                               (:isrecurring new-goal)
                               (:ispublic new-goal)
                               (:iscompleted new-goal))
        result (db/coerce-timestamps goal)]
    result))

(defn update! [goal]
  (try
    (db/update-goal! db/db-connection
                     (:templateid goal)
                     (:parentid goal)
                     (:ownerid goal)
                     (:title goal)
                     (:flow goal)
                     (c/to-sql-time (:startdate goal))
                     (c/to-sql-time (:enddate goal))
                     (c/to-sql-time (:completiondate goal))
                     (name (:category goal))
                     (name (:difficulty goal))
                     (:description goal)
                     (:reason goal)
                     (:isrecurring goal)
                     (:ispublic goal)
                     (:iscompleted goal)
                     (:id goal))
    (catch java.sql.BatchUpdateException e
      (println e)
      (.getNextException e))))

;; Routes

(defroutes* apiroutes
  (context* "/goals" []
            :tags ["goals"]
            (GET* "/" []
                  :return   [Goal]
                  :summary  "Gets all Goals"
                  (ok (get-goals)))
            (GET* "/:id" []
                  :path-params [id :- Long]
                  :return   Goal
                  :summary  "Gets a goal"
                  (ok (get-goal id)))
            (POST* "/" []
                   :return   Goal
                   :body     [goal (rs/describe NewGoal "new goal")]
                   :summary  "Adds a goal"
                   (ok (add! goal)))
            (PUT* "/" []
                  :body     [goal Goal]
                  :summary  "Updates a goal"
                  (update! goal)
                  (ok))
            (DELETE* "/:id" []
                     :path-params [id :- Long]
                     :summary  "Deletes a Goal"
                     (delete! id)
                     (ok))))

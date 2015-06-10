(ns levelup.goal
  (:require [schema.core :as s]
            [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [clj-time.format :as f]
            [clj-time.coerce :as c]
            [levelup.data :as db]
            [levelup.auth.access-control :as access]
            [ring.swagger.schema :as rs])
  (:import [java.sql Timestamp]))

(s/defschema ResponseGoal {:id Long
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
                           (s/optional-key :followers) Integer
                           :isrecurring Boolean
                           :ispublic Boolean
                           :iscompleted Boolean})

(s/defschema RequestGoal (dissoc ResponseGoal :ownerid (s/optional-key :followers)))
(s/defschema NewGoal (dissoc RequestGoal :id))

;; Domain funcs

(defn get-goal [id]
  (first (map db/coerce-timestamps
              (db/get-goal db/db-connection id))))

(defn get-goals []
  (map db/coerce-timestamps
       (db/get-all-goal-templates db/db-connection)))

(defn delete! [id]
  (let [goal (db/get-goal db/db-connection id)]
    (when (:templateid goal)
      (db/dec-goal-followers! db/db-connection (:templateid goal))))
  (db/delete-goal! db/db-connection id))

(defn add! [new-goal ownerid]
  (let [goal (db/insert-goal<! db/db-connection
                               (:templateid new-goal)
                               (:parentid new-goal)
                               ownerid
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

    (when (:templateid goal)
      (db/inc-goal-followers! db/db-connection (:templateid goal)))
    result))

(defn update! [goal]
  (try
    (db/update-goal! db/db-connection
                     (:templateid goal)
                     (:parentid goal)
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
                  :return   [ResponseGoal]
                  :summary  "Gets all Goals"
                  (ok (get-goals)))
            (GET* "/:goal-id" []
                  :path-params [goal-id :- Long]
                  :return   ResponseGoal
                  :summary  "Gets a goal"
                  (ok (get-goal goal-id)))
            (POST* "/" request
                   :return   ResponseGoal
                   :body     [goal (rs/describe NewGoal "new goal")]
                   :summary  "Adds a goal"
                   :middlewares [access/authenticated-user]
                   (ok (add! goal (:identity request))))
            (PUT* "/:goal-id" []
                  :path-params [goal-id :- Long]
                  :body     [goal RequestGoal]
                  :summary  "Updates a goal"
                  :middlewares [access/authenticated-user access/is-that-users-goal]
                  (update! goal)
                  (ok))
            (DELETE* "/:goal-id" []
                     :path-params [goal-id :- Long]
                     :summary  "Deletes a Goal"
                     :middlewares [access/authenticated-user access/is-that-users-goal]
                     (delete! goal-id)
                     (ok))))

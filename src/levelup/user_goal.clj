(ns levelup.user-goal
  (:require [schema.core :as s]
            [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [clj-time.format :as f]
            [clj-time.coerce :as c]
            [levelup.data :as db]
            [levelup.goal :as goal]
            [ring.swagger.schema :as rs])
  (:import [java.sql Timestamp]))

;; Domain funcs

(defn get-goal [id]
  (goal/get-goal id))

(defn get-goals [uid]
  (map db/coerce-timestamps
       (db/get-all-user-goals db/db-connection uid)))

(defn delete! [id]
  (goal/delete! id))

(defn add! [new-goal]
  (goal/add! new-goal))

(defn update! [goal]
  (goal/update! goal)

;; Routes

(defroutes* apiroutes
  (context* "/users" []
            (context* "/:user-id" []
                      (context* "/goals" []
                                :tags ["users"]
                                (GET* "/" request
                                      :path-params [user-id :- Long]
                                      :return   [goal/Goal]
                                      :summary  "Gets all goals belonging to user"
                                      (ok (get-goals user-id)))
                                (GET* "/:id" []
                                      :path-params [id :- Long]
                                      :return   goal/Goal
                                      :summary  "Gets a goal belonging to user"
                                      (ok (get-goal id)))
                                (POST* "/" []
                                       :return   goal/Goal
                                       :body     [goal (rs/describe goal/NewGoal "new goal")]
                                       :summary  "Adds a goal to user's list"
                                       (ok (add! goal)))
                                (PUT* "/" []
                                      :body     [goal goal/Goal]
                                      :summary  "Updates a goal in user's list"
                                      (update! goal)
                                      (ok))
                                (DELETE* "/:id" []
                                         :path-params [id :- Long]
                                         :summary  "Deletes a goal from user's list"
                                         (ok (delete! id)))))))


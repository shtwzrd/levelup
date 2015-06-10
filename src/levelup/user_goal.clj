(ns levelup.user-goal
  (:require [schema.core :as s]
            [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [clj-time.format :as f]
            [clj-time.coerce :as c]
            [levelup.data :as db]
            [levelup.goal :as goal]
            [levelup.auth.access-control :as access]
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

(defn add! [new-goal ownerid]
  (goal/add! new-goal ownerid))

(defn update! [goal]
  (goal/update! goal))

;; Routes

(defroutes* apiroutes
  (context* "/users" []
            (context* "/:user-id" []
                      (context* "/goals" []
                                :tags ["users"]
                                (GET* "/" request
                                      :path-params [user-id :- Long]
                                      :return   [goal/ResponseGoal]
                                      :summary  "Gets all goals belonging to user"
                                      :middlewares [access/authenticated-user access/is-that-user]
                                      (ok (get-goals user-id)))
                                (GET* "/:goal-id" []
                                      :path-params [goal-id :- Long]
                                      :return   goal/ResponseGoal
                                      :summary  "Gets a goal belonging to user"
                                      :middlewares [access/authenticated-user access/is-that-users-goal]
                                      (ok (get-goal goal-id)))
                                (POST* "/" request
                                       :return   goal/ResponseGoal
                                       :body     [goal (rs/describe goal/NewGoal "new goal")]
                                       :summary  "Adds a goal to user's list"
                                       :middlewares [access/authenticated-user access/is-that-user]
                                       (ok (add! goal (:identity request))))
                                (PUT* "/:goal-id" []
                                      :path-params [goal-id :- Long]
                                      :body     [goal goal/RequestGoal]
                                      :summary  "Updates a goal in user's list"
                                      :middlewares [access/authenticated-user access/is-that-users-goal]
                                      (update! goal)
                                      (ok))
                                (DELETE* "/:goal-id" []
                                         :path-params [goal-id :- Long]
                                         :summary  "Deletes a goal from user's list"
                                         :middlewares [access/authenticated-user access/is-that-users-goal]
                                         (delete! goal-id)
                                         (ok))))))


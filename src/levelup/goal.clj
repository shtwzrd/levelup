(ns levelup.goal
  (:require [schema.core :as s]
            [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [ring.swagger.schema :as rs :refer [describe]]))

(s/defschema Goal {:id Long
                   :title String
                   :description String
                   :completed? Boolean})

(s/defschema NewGoal (dissoc Goal :id))

;; Test 'DB'

(defonce id-seq (atom 0))
(defonce goals (atom (array-map)))

;; Domain funcs

(defn get-goal [id] (@goals id))
(defn get-goals [] (-> goals deref vals reverse))
(defn delete! [id] (swap! goals dissoc id) nil)

(defn add! [new-goal]
  (let [id (swap! id-seq inc)
        goal (rs/coerce! Goal (assoc new-goal :id id))]
    (swap! goals assoc id goal)
    goal))

(defn update! [goal]
  (let [goal (rs/coerce! Goal goal)]
    (swap! goals assoc (:id goal) goal)
    (get-goal (:id goal))))

;; Data

(when (empty? @goals)
  (add! {:title "all base are belong" :description "for great justice" :completed? true})
  (add! {:title "sob uncontrollably" :description "faarkrog doesn't love me enough" :completed? false}))

;; Routes

(defroutes* goal-routes
  (context "/goals" []
           (context "/goals" []
                    (GET* "/" []
                          :return   [Goal]
                          :summary  "Gets all Goals"
                          :nickname "getGoals"
                          (ok (get-goals)))
                    (GET* "/:id" []
                          :path-params [id :- Long]
                          :return   Goal
                          :summary  "Gets a goal"
                          :nickname "getGoal"
                          (ok (get-goal id)))
                    (POST* "/" []
                           :return   Goal
                           :body     [goal (describe NewGoal "new goal")]
                           :summary  "Adds a goal"
                           :nickname "addGoal"
                           (ok (add! goal)))
                    (PUT* "/" []
                          :return   Goal
                          :body     [goal Goal]
                          :summary  "Updates a goal"
                          :nickname "updateGoal"
                          (ok (update! goal)))
                    (DELETE* "/:id" []
                             :path-params [id :- Long]
                             :summary  "Deletes a Goal"
                             :nickname "deleteGoal"
                             (ok (delete! id))))))

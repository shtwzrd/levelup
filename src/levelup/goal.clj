(ns levelup.goal
  (:require [schema.core :as s]
            [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [ring.swagger.schema :as rs]))

(s/defschema Goal {:id Long
                   (s/optional-key :templateid) Long
                   (s/optional-key :parentid) Long
                   :owner-id Long
                   :title String
                   :sequence Long
                   :start org.joda.time.DateTime
                   (s/optional-key :end) org.joda.time.DateTime
                   (s/optional-key :completion-date) org.joda.time.DateTime
                   :category (s/enum :health :spirit :knowledge :finance :happiness :social)
                   :difficulty (s/enum :trivial :simple :average :huge :colossal)
                   (s/optional-key :description) String
                   (s/optional-key :reason) String
                   :recurring? Boolean
                   :public? Boolean
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
  (add! {:title "all base are belong" :owner-id 1 :sequence 0 :description "for great justice" :completed? true :recurring? false :public? false :difficulty :trivial :category :spirit :start "2015-05-05"}))

;; Routes

(defroutes* goal-routes
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
                  :return   Goal
                  :body     [goal Goal]
                  :summary  "Updates a goal"
                  (ok (update! goal)))
            (DELETE* "/:id" []
                     :path-params [id :- Long]
                     :summary  "Deletes a Goal"
                     (ok (delete! id)))))

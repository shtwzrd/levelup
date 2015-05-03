(ns levelup.goal
  (:require [schema.core :as s]
            [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [clj-time.format :as f]
            [clj-time.coerce :as c]
            [levelup.data :as data-access]
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

;; Test 'DB'

(defonce id-seq (atom 0))
(defonce goals (atom (array-map)))

; Domain funcs

(defn get-goal [id] (@goals id))
(defn get-goals [] (-> goals deref vals reverse))
(defn delete! [id] (swap! goals dissoc id) nil)

(defn add! [new-goal]
  (println new-goal)
  (let [goal (data-access/insert-goal<! data-access/db-connection
                                        (:templateid new-goal)
                                        (:parentid new-goal)
                                        (:ownerid new-goal)
                                        (:title new-goal)
                                        (:flow new-goal)
                                        (new Timestamp (c/to-long (f/parse (:startdate new-goal))))
                                        (new Timestamp (c/to-long (f/parse (:enddate new-goal))))
                                        (new Timestamp (c/to-long (f/parse (:completiondate new-goal))))
                                        (name (:category new-goal))
                                        (name (:difficulty new-goal))
                                        (:description new-goal)
                                        (:reason new-goal)
                                        (:isrecurring new-goal)
                                        (:ispublic new-goal)
                                        (:iscompleted new-goal))]
    goal))

(defn update! [goal]
  (let [goal (rs/coerce! Goal goal)]
    (swap! goals assoc (:id goal) goal)
    (get-goal (:id goal))))

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

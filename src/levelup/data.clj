(ns levelup.data
  (:require [schema.core :as s]
            [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [ring.swagger.schema :as rs]))

(s/defschema User {:id Long
                   (s/optional-key :password) String
                   :email String})

(s/defschema NewUser (dissoc User :id))


;; Test 'DB'

(defonce id-seq (atom 0))
(defonce users (atom (array-map)))

;; Domain funcs

(defn get-user [id] (@users id))
(defn get-users [] (-> users deref vals reverse))
(defn delete! [id] (swap! users dissoc id) nil)

(defn add! [new-user]
  (let [id (swap! id-seq inc)
        user (rs/coerce! User (assoc new-user :id id))]
    (swap! users assoc id user)
    user))

(defn update! [user]
  (let [user (rs/coerce! User user)]
    (swap! users assoc (:id user) user)
    (get-user (:id user))))

;; Data

(when (empty? @users)
  (add! {:email "test@tester.com" :password "secret"})
  (add! {:email "admin@tester.com" :password "secret"}))

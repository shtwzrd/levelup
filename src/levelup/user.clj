(ns levelup.user
  (:require [schema.core :as s]
            [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [ring.swagger.schema :as rs]
            [levelup.goal :as goals]))

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

;; Routes

(defroutes* user-routes
  (context* "/users" []
            :tags ["users"]
            (GET* "/" []
                  :return   [User]
                  :summary  "Gets all users"
                  (ok (get-users)))
            (GET* "/:id" []
                  :path-params [id :- Long]
                  :return   User
                  :summary  "Gets a user"
                  (ok (get-user id)))
            (POST* "/" []
                   :return   User
                   :body     [user (rs/describe NewUser "new user")]
                   :summary  "Adds a user"
                   (ok (add! user)))
            (PUT* "/" []
                  :return   User
                  :body     [user User]
                  :summary  "Updates a user"
                  (ok (update! user)))
            (DELETE* "/:id" []
                     :path-params [id :- Long]
                     :summary  "Deletes a user"
                     (ok (delete! id)))))

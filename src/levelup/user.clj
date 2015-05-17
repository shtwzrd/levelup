(ns levelup.user
  (:require [schema.core :as s]
            [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [ring.swagger.schema :as rs]
            [levelup.data :as db]
            [buddy.hashers :as sec]
            [levelup.goal :as goals]))

(s/defschema User {:id Long
                   (s/optional-key :secret) String
                   (s/optional-key :displayname) String
                   (s/optional-key :social) Integer
                   (s/optional-key :health) Integer
                   (s/optional-key :happiness) Integer
                   (s/optional-key :finance) Integer
                   (s/optional-key :knowledge) Integer
                   (s/optional-key :spirit) Integer
                   :email String})

(s/defschema NewUser {:email String
                      :displayname String
                      :secret String})

;; Domain funcs

(defn get-user-by-id [id]
  (first (map db/coerce-timestamps (db/get-user-by-id db/db-connection id))))

(defn get-user-by-email [email]
  (first (map db/coerce-timestamps (db/get-user-by-email db/db-connection email))))

(defn get-users []
  (map db/coerce-timestamps
       (db/get-all-users db/db-connection)))

(defn delete! [id]
  (db/delete-user! db/db-connection id))

(defn add-with-basic-secret! [new-user]
  (let [user (db/insert-user-with-basic-secret<! db/db-connection
                                                 (:email new-user)
                                                 (:displayname new-user)
                                                 (sec/encrypt (:secret new-user)))
        user-result (db/coerce-timestamps user)]
    (dissoc user-result :basic_secret :registrationdate)))

(defn update! [user]
  (try
    (db/update-user-by-id! db/db-connection
                           (:email user)
                           (:displayname user)
                           (:id user))
    (catch java.sql.BatchUpdateException e
      (println e)
      (.getNextException e))))

;; Routes

(defroutes* apiroutes
  (context* "/users" []
            :tags ["users"]
            (GET* "/" []
                  :return   [User]
                  :summary  "Gets all users"
                  (ok (get-users)))
            (GET* "/login" []
                  :summary "Returns 200 on a successfully authenticated request"
                  (ok))
            (POST* "/register-basic" []
                   :return   User
                   :summary "Registers a new user with a password"
                   :body     [user (rs/describe NewUser "new user")]
                   (ok (add-with-basic-secret! user)))
            (GET* "/:id" []
                  :path-params [id :- Long]
                  :return   User
                  :summary  "Gets the user with the corresponding id"
                  (ok (get-user-by-id id)))
            (GET* "/:email" []
                  :path-params [email :- String]
                  :return   User
                  :summary  "Gets the user with the corresponding email address"
                  (ok (get-user-by-email email)))
            (PUT* "/" []
                  :return   User
                  :body     [user User]
                  :summary  "Updates a user"
                  (update! user)
                  (ok))
            (DELETE* "/:id" []
                     :path-params [id :- Long]
                     :summary  "Deletes a user"
                     (delete! id)
                     (ok))))

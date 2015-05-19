(ns levelup.user
  (:require [schema.core :as s]
            [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [ring.swagger.schema :as rs]
            [levelup.data :as db]
            [buddy.hashers :as sec]
            [levelup.auth.access-control :as access]
            [levelup.goal :as goals]))

(s/defschema FullUser {:id Long
                       (s/optional-key :displayname) String
                       :social Integer
                       :health Integer
                       :happiness Integer
                       :finance Integer
                       :knowledge Integer
                       :spirit Integer
                       (s/optional-key :secret) String
                       (s/optional-key :email) String})

(s/defschema ResponseUser (dissoc FullUser (s/optional-key :secret)))

(s/defschema RequestUser
  (dissoc FullUser :social :health :happiness :finance :knowledge :spirit))

(s/defschema NewUser (dissoc RequestUser :id))

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
                  :return   [ResponseUser]
                  :summary  "Gets all users"
                  :middlewares [access/authenticated-user]
                  (ok (get-users)))
            (GET* "/login" request
                  :summary "Returns status code 200 on a successfully authenticated request"
                  :middlewares [access/authenticated-user]
                  (ok {}))
            (POST* "/register-basic" []
                   :return   ResponseUser
                   :summary "Registers a new user with a password"
                   :body     [user (rs/describe NewUser "new user")]
                   (ok (add-with-basic-secret! user)))
            (GET* "/:user-id" []
                  :path-params [user-id :- Long]
                  :return   ResponseUser
                  :summary  "Gets the user with the corresponding id"
                  :middlewares [access/authenticated-user]
                  (ok (get-user-by-id user-id)))
            (GET* "/by-email/:email" []
                  :path-params [email :- String]
                  :return   ResponseUser
                  :summary  "Gets the user with the corresponding email address"
                  :middlewares [access/authenticated-user]
                  (ok (get-user-by-email email)))
            (PUT* "/" []
                  :return   ResponseUser
                  :body     [user RequestUser]
                  :summary  "Updates a user"
                  :middlewares [access/authenticated-user access/is-that-user]
                  (update! user)
                  (ok {}))
            (DELETE* "/:user-id" []
                     :path-params [user-id :- Long]
                     :summary  "Deletes a user"
                     :middlewares [access/authenticated-user access/is-that-user]
                     (delete! user-id)
                     (ok {}))))

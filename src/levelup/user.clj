(ns levelup.user
  (:require [schema.core :as s]
            [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [ring.swagger.schema :as rs]
            [buddy.auth.accessrules :refer [restrict]]
            [levelup.auth.handlers :as auth]
            [levelup.data :refer :all]
            [levelup.goal :as goals]))

;; Routes

(defroutes* user-routes
  (context* "/users" []
            :tags ["users"]
            (GET* "/" []
                  :return   [User]
                  :summary  "Gets all users"
                  (ok (get-users)))
            (GET* "/login" []
                  :summary "Returns 200 on a successfully authenticated request"
                  (ok))
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

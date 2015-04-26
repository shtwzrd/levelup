(ns levelup.handler
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [schema.core :as s]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [buddy.auth.backends.httpbasic :refer [http-basic-backend]]
            [buddy.auth.backends.token :refer [token-backend]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [levelup.user :as user]
            [levelup.goal :as goal]))

;; Define function that is responsible of authenticating requests.
;; In this case it receives a map with username and password and i
;; should return a value that can be considered a "user" instance
;; and should be a logical true.

(defn authenticate
  [request authdata]
  (let [username (:username authdata)
        password (:password authdata)]
    (let [user-password (:password (user/get-user (read-string username)))]
      (when (= user-password password)
        username))))

;; Create an instance of auth backend without explicit handler for
;; unauthorized request. (That leaves the responsability to default
;; backend implementation.

(def auth-backend
  (http-basic-backend {:realm "levelup"
                       :authfn authenticate}))


(defapi app
  (swagger-ui)
  (swagger-docs
   {:info {:version "1.0.0"
           :title "Levelup API"
           :description "beep boop"}}
   {:tags [{:name "goals", :description "managing goals"}
           {:name "users", :description "managing users"}
           {:name "util", :description "bonus utilities"}]})
  (context "/api" []
           (context "/v1" []
                     goal/goal-routes
                     user/user-routes
                     (context* "/util" []
                               :tags ["util"]
                               (GET* "/echo" request
                                     :return String
                                     :query-params []
                                     :summary "echos the request back as json"
                                     :middlewares [(wrap-authentication auth-backend)]
                                     (if-not (authenticated? request)
                                       (forbidden (str "Unauthorized" " " request))
                                       (ok (str request)))))
                     (context* "/users" []
                               (context* "/:user-id" []
                                         goal/goal-routes)))))

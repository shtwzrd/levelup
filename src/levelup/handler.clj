(ns levelup.handler
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [schema.core :as s]
            [ring.swagger.schema :as rs]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [buddy.auth.backends.httpbasic :refer [http-basic-backend]]
            [buddy.auth.backends.token :refer [token-backend]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [levelup.goal :refer :all]))

;; DATA - Placeholder secrets
(def authdata {:admin "secret"
               :test "secret"})

;; Define function that is responsible of authenticating requests.
;; In this case it receives a map with username and password and i
;; should return a value that can be considered a "user" instance
;; and should be a logical true.

(defn my-authfn
  [req {:keys [username password]}]
  (when-let [user-password (get authdata (keyword username))]
    (when (= password user-password)
      (keyword username))))

;; Create an instance of auth backend without explicit handler for
;; unauthorized request. (That leaves the responsability to default
;; backend implementation.

(def auth-backend
  (http-basic-backend {:realm "MyExampleSite"
                       :authfn my-authfn}))

(defapi app
  (swagger-docs)
  (context "/api" []
           (context "/v1" []
                    (swagger-ui)
                    (swaggered "goal"
                               :description "Goal api"
                               goal-routes)

                    (swaggered "util"
                               :description "etc"
                               (context "/util" []
                                        (ANY* "/echo" request
                                              :return String
                                              :query-params []
                                              :summary "echos the request back as json"
                                              :middlewares [(wrap-authentication auth-backend)]
                                              (if-not (authenticated? request)
                                                (forbidden (str "Unauthorized"))
                                                (ok (str request)))
                                              ))))))

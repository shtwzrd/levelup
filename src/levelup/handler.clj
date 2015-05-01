(ns levelup.handler
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [ring.util.response :refer (response redirect)]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [buddy.auth.accessrules :refer [restrict wrap-access-rules]]
            [levelup.user :as user]
            [levelup.goal :as goal]
            [levelup.auth.handlers :refer :all]
            [levelup.auth.core :refer :all]))

(defapi swag-app
  (swagger-ui)
  (swagger-docs
   {:info {:version "1.0.0"
           :title "Levelup API"
           :description "beep boop"}}
   {:tags [{:name "goals", :description "managing goals"}
           {:name "users", :description "managing users"}
           {:name "util", :description "bonus utilities"}]})
  (context* "/api" []
            (context* "/v1" []
                      goal/goal-routes
                      user/user-routes
                      (context* "/util" []
                                :tags ["util"]
                                (GET* "/echo" request
                                      :return String
                                      :query-params []
                                      :summary "echos the request back as json"
                                      (ok (str request))))
                      (context* "/users" []
                                (context* "/:user-id" []
                                          goal/goal-routes)))))

(def app (-> swag-app
             (wrap-access-rules auth-rules)
             (wrap-authentication auth-backend)))

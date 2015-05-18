(ns levelup.handler
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [ring.util.response :refer (response redirect)]
            [buddy.auth.middleware :refer [wrap-authentication]]
            [levelup.user :as user]
            [levelup.goal :as goal]
            [levelup.user-goal :as user-goal]
            [levelup.auth.core :refer :all]))

(defapi swag-app
  (swagger-ui)
  (swagger-docs
   {:info {:version "1.0.0"
           :title "Levelup API" }}
   {:tags [{:name "goals", :description "managing goal templates"}
           {:name "users", :description "managing users and their goals"}
           {:name "util", :description "bonus utilities"}]})
  (context* "/api" []
            (context* "/v1" []
                      goal/apiroutes
                      user/apiroutes
                      user-goal/apiroutes
                      (context* "/util" []
                                :tags ["util"]
                                (GET* "/echo" request
                                      :return String
                                      :query-params []
                                      :summary "echos the request back as json"
                                      (ok (str request)))))))

(def app (-> swag-app
             (wrap-authentication auth-backend)))

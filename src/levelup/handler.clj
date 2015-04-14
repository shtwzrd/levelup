(ns levelup.handler
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [schema.core :as s]
            [ring.swagger.schema :refer [field describe]]
            [levelup.goal :refer :all]))

(s/defschema Message {:message String})

(defapi app
  (swagger-ui)
  (swagger-docs
   :title "Levelup")


  (swaggered "goal"
             :description "Goal api"
             goal-routes)

  (swaggered "etc"
             :description "greetings and salutations"
             (context "/greetings" []

                      (GET* "/hello" []
                            :return Message
                            :query-params [name :- String]
                            :summary "say hello"
                            (ok {:message (str "Hello, " name)}))

                      (GET* "/salutations" []
                            :return Message
                            :query-params [name :- String]
                            :summary "say greetings"
                            (ok {:message (str "Greetings, " name)})))

             (context "/util" []
                      (ANY* "/echo" request
                            :return String
                            :query-params []
                            :summary "echos the request back as json"
                            (ok (str request)))
                      )))


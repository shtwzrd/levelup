(ns levelup.auth.access-control
  (:require [buddy.auth :refer [authenticated? throw-unauthorized]]
            [buddy.auth.backends.httpbasic :refer [http-basic-backend]]
            [buddy.auth.backends.token :refer [token-backend]]
            [ring.util.http-response :refer :all]
            [levelup.data :as db]))

(defn authenticated-user [handler]
  (fn [request]
    (if (authenticated? request)
      (handler request)
      (forbidden "Access Denied: Unauthorized"))))

(defn is-that-user [handler]
  (fn [request]
    (let [cred (:identity request)
          id (Integer/parseInt (:user-id (:route-params request)))]
      (if (= id cred)
        (handler request)
        (forbidden "Access Denied: Unauthorized")))))

(defn is-that-users-goal [handler]
  (fn [request]
    (let [cred (:identity request)
          owner (db/get-ownerid (Integer/parseInt(:goal-id (:route-params request))))]
      (if (= cred owner)
        (handler request)
        (forbidden "Access Denied: Unauthorized")))))

(ns levelup.auth.access-control
  (:require [buddy.auth :refer [authenticated? throw-unauthorized]]
            [buddy.auth.backends.httpbasic :refer [http-basic-backend]]
            [buddy.auth.backends.token :refer [token-backend]]
            [ring.util.response :refer [response status]]
            [levelup.data :as db]))

(defn authenticated-user [handler]
  (fn [request]
    (println request)
    (if (authenticated? request)
      (handler request)
      (-> (response "Access Denied: Failed to authenticate")
          (status 403)))))

(defn is-that-user [handler]
  (fn [request]
    (let [cred (:identity request)
          id (:user-id (:route-params request))]
      (if (= id cred)
        (handler request)
        (-> (response "Access Denied: Unauthorized")
            (status 403))))))

(defn is-that-users-goal [handler]
  (fn [request]
    (let [cred (:identity request)
          owner (db/get-ownerid (Integer/parseInt(:user-id (:route-params request))))]
      (println request)
      (if (= cred owner)
        (handler request)
        (-> (response "Access Denied: Unauthorized")
            (status 403))))))

(ns levelup.auth.core
  (:require [buddy.auth :refer [authenticated? throw-unauthorized]]
            [buddy.auth.backends.httpbasic :refer [http-basic-backend]]
            [buddy.auth.backends.token :refer [token-backend]]
            [buddy.hashers :as sec]
            [levelup.data :as data]))

(defn authentication-fn
  [request authdata]
  (let [id (:username authdata)
        secret (:password authdata)
        user-secret (first (data/get-user-secrets-by-email data/db-connection id))]
    (when user-secret
      (when (sec/check secret (:basic_secret user-secret))
        (:id (first (data/get-user-by-email data/db-connection id)))))))

(def auth-backend
  (http-basic-backend {:authfn authentication-fn}))

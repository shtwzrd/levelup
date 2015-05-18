(ns levelup.auth.core
  (:require [buddy.auth :refer [authenticated? throw-unauthorized]]
            [buddy.auth.backends.httpbasic :refer [http-basic-backend]]
            [buddy.auth.backends.token :refer [token-backend]]
            [buddy.hashers :as sec]
            [levelup.data :as data]))

(defn authentication-fn
  [request authdata]
  (let [uid (:username authdata)
        secret (:password authdata)
        user-secret (first (data/get-user-secrets data/db-connection (Integer/parseInt uid)))]
    (when user-secret
      (when (sec/check secret (:basic_secret user-secret))
        uid))))

(def auth-backend
  (http-basic-backend {:authfn authentication-fn}))

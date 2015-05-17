(ns levelup.auth.core
  (:require [buddy.auth :refer [authenticated? throw-unauthorized]]
            [buddy.auth.backends.httpbasic :refer [http-basic-backend]]
            [buddy.auth.backends.token :refer [token-backend]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [buddy.auth.accessrules :as ar]
            [buddy.hashers :as sec]
            [levelup.data :as data]))

(defn authentication-fn
  [request authdata]
  (let [uid (:username authdata)
        secret (:password authdata)]
    (let [user-secret
          (:basic_secret (data/get-user-secrets data/db-connection (read-string uid)))]
      (when user-secret
        (when (sec/check secret user-secret)
          uid)))))

(def auth-backend
  (http-basic-backend {:authfn authentication-fn}))

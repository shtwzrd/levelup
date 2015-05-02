(ns levelup.auth.core
  (:require [buddy.auth :refer [authenticated? throw-unauthorized]]
            [buddy.auth.backends.httpbasic :refer [http-basic-backend]]
            [buddy.auth.backends.token :refer [token-backend]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [buddy.auth.accessrules :as ar]
            [levelup.data :as data]
            [levelup.user :as user]))

(defn authentication-fn
  [request authdata]
  (let [username (:username authdata)
        password (:password authdata)]
    (let [user-password (:password (user/get-user (read-string username)))]
      (when (= user-password password)
        username))))

(def auth-backend
  (http-basic-backend {:authfn authentication-fn}))

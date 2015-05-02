(ns levelup.auth.handlers
  (:require [buddy.auth :refer [authenticated? throw-unauthorized]]
            [buddy.auth.backends.httpbasic :refer [http-basic-backend]]
            [buddy.auth.backends.token :refer [token-backend]]
            [buddy.auth.accessrules :as ar]
            [clojure.string :refer [split]]))

(defn authenticated-user
  [request]
  (if (authenticated? request)
    true
    (ar/error "Not authenticated")))

(defn is-that-user
  [request]
  (let [id (:identity request)
        uri (last (split (:uri request) #"/"))]
    (if (= id uri)
      true
      (ar/error "Not authorized"))))


(defn no-access
  [request]
  (if false
    true
    (ar/error "access never allowed")))

(defn all-access
  [request]
  true)

(defn unauthorized-handler
  [request value]
  {:status 403
   :headers {}
   :body "Not authorized"})

(def auth-rules {:rules [{:uri "/api/v1/users/login"
                          :handler authenticated-user}
                         {:pattern #"/api/v1/users/[0-9]+$"
                          :handler {:and [authenticated-user is-that-user]}}]
                 :on-error unauthorized-handler})


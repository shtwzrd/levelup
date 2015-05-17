(defproject levelup "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring/ring-servlet "1.3.2"]
                 [clj-time "0.9.0"]
                 [buddy/buddy-auth "0.5.1"]
                 [buddy/buddy-hashers "0.4.2"]
                 [prismatic/schema "0.4.1"]
                 [yesql "0.4.0"]
                 [environ "1.0.0"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]
                 [metosin/compojure-api "0.20.0"]
                 [metosin/ring-http-response "0.6.1"]
                 [metosin/ring-swagger-ui "2.1.1-M2"]]
  :ring {:handler levelup.handler/app}
  :uberjar-name "server.jar"
  :profiles {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]]
                   :plugins [[lein-ring "0.9.2"]]}}
  :min-lein-version "2.0.0"
  )



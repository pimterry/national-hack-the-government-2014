(defproject stats-api "0.1.0-SNAPSHOT"
  :description "A Compojure app that aggregates election results data"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [ring/ring-core "1.2.0"]
                 [ring/ring-jetty-adapter "1.2.0"]
                 [compojure "1.1.3"]
                 [ring/ring-json "0.2.0"]
                 [ring-cors "0.1.0"]
                 [clojurewerkz/neocons "2.0.1"]
                 [environ "0.4.0"]
                 [incanter "1.5.4"]]
  :plugins [[lein-environ "0.4.0"]]
  :min-lein-version "2.0.0"
  :main stats-api.routes
  :profiles
    {:dev {:env {:graphenedb-url "http://localhost:7474/db/data"}}}
  )

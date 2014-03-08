(defproject stats-api "0.1.0-SNAPSHOT"
  :description "A Compojure app that aggregates election results data"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [ring/ring-core "1.2.0"]
                 [ring/ring-jetty-adapter "1.2.0"]
                 [compojure "1.1.3"]
                 [ring/ring-json "0.2.0"]]
  :main stats-api.routes
  )

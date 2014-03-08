(ns stats-api.data-layer.connection-config
  (:require [clojurewerkz.neocons.rest :as nr]
            [clojurewerkz.neocons.rest.cypher :as cy]
            [environ.core :as environ]))

(defn connection-string [] (nr/connect! (environ/env :graphenedb-url)))

(defn run-query [query]
  (connection-string)
  (cy/tquery query)
)

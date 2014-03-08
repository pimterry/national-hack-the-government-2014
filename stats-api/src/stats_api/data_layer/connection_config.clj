(ns stats-api.data-layer.connection-config
  (:require [clojurewerkz.neocons.rest :as nr]
            [clojurewerkz.neocons.rest.cypher :as cy]))

(defn connection-string [] (nr/connect! "http://localhost:7474/db/data/"))

(defn run-query [query]
  (connection-string)
  (cy/tquery query)
  )

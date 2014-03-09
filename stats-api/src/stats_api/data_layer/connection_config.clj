(ns stats-api.data-layer.connection-config
  (:require [clojurewerkz.neocons.rest :as nr]
            [clojurewerkz.neocons.rest.cypher :as cy]
            [environ.core :as environ]))

(let [env (get (System/getenv) "GRAPHENEDB_URL" "http://localhost:7474/db/data")]
  (defn connection-string [] (nr/connect! env)))

(defn run-query [query]
  (connection-string)
  (cy/tquery query)
)

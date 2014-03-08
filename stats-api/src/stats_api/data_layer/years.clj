(ns stats-api.data-layer.years
  (:use stats-api.data-layer.connection-config)
  (:require [clojurewerkz.neocons.rest.cypher :as cy]))

(defn fetch-years []
  (connection-string)
  (cy/tquery "MATCH (y:year) RETURN y.name AS name ORDER BY y.name")
)
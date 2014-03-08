(ns stats-api.data-layer.years
   (:use stats-api.data-layer.connection-config)
   )

(defn fetch-years []
  (run-query "MATCH (y:year) RETURN y.name AS name")
)
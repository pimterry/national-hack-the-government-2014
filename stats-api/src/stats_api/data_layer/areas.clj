(ns stats-api.data-layer.areas
  (:use stats-api.data-layer.connection-config)
  )

(defn fetch-areas []
  (run-query "MATCH (a:area) RETURN a.name AS name")
  )
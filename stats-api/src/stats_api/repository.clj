(ns stats-api.repository
  (:use stats-api.data-layer.connection-config))

  (defn years [] (run-query "MATCH (y:year) RETURN y.name AS name"))

  (defn areas [] (run-query "MATCH (a:area) RETURN a.name AS name, id(a) AS id"))

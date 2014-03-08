(ns stats-api.data-layer.repository
  (:use stats-api.data-layer.connection-config))

  (defn years [] (run-query "MATCH (y:year) RETURN y.name AS name"))

  (defn areas [] (run-query "MATCH (a:area) RETURN a.name AS name, id(a) AS id"))

  (defn politician [] (run-query "MATCH (p:politician) RETURN p.name AS name, id(p) AS id"))

  (defn parties [] (run-query "MATCH (p:party) RETURN p.name AS name"))

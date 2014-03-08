(ns stats-api.repository
  (:use stats-api.data-layer.years))

  (defn years [] [(fetch-years)])
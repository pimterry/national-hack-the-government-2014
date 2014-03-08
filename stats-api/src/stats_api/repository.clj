(ns stats-api.repository
  (:use stats-api.data-layer.years)
  (:use stats-api.data-layer.areas))

  (defn years [] [(fetch-years)])

  (defn areas [] [(fetch-areas)])

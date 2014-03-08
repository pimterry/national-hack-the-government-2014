(ns stats-api.data-layer.connection-config
  (:require [clojurewerkz.neocons.rest :as nr]))

(defn connection-string [] (nr/connect! "http://localhost:7474/db/data/"))

(ns stats-api.statistical-logic.surprise
  (:use [incanter core stats charts]
        [stats-api.data-layer.repository],
        [stats-api.statistical-logic.expectedresult]))


(defn surprise [year areaId politicianId]

  (Deviation year areaId (partyForPolitician politicianId))
)

(defn asPerc [num dec]
  (* 100 (/ num (if (= 0 dec) 1 dec)))
)




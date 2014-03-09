(ns stats-api.statistical-logic.surprise
  (:use [incanter core stats charts]
        [stats-api.data-layer.repository],
        [stats-api.statistical-logic.expectedresult]))

(defn binParam [year areaId politicianId]
  (get
    (first
      (filter (fn[x] (=
                 (get x "partyName") (partyForPolitician politicianId)
              ))
              (multivariate-parameters year areaId)
      )
    )
  "numVotes")
)

(defn rawExpected [year areaId politicianId]
  (*(sumResults (wardResults year areaId)) (binParam year areaId politicianId))
)

(defn surprise [year areaId politicianId]
  (cdf-binomial
    (electionResult year areaId politicianId)
    :size (sumResults (wardResults year areaId))
    :prob (binParam year areaId politicianId)
  )
)

(defn formatted-surprise [year areaId politicianId]
  (if (< (rawExpected year areaId politicianId) (electionResult year areaId politicianId))
    (surprise year areaId politicianId)
    (- (surprise year areaId politicianId) 1)
  )
)

(defn asPerc [num dec]
  (* 100 (/ num (if (= 0 dec) 1 dec)))
)

(defn expected-votes [year areaId politicianId]
  {:raw (rawExpected year areaId politicianId)
   :perc (asPerc
            (rawExpected year areaId politicianId)
            (sumResults (wardResults year areaId))
          )
  }
)

(defn actual-votes [year areaId politicianId]
  {:raw (electionResult year areaId politicianId)
   :perc (asPerc (electionResult year areaId politicianId)
                 (sumResults (wardResults year areaId))
         )
  }
)




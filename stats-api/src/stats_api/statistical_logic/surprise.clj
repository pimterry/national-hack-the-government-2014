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

(defn surprise [year areaId politicianId] (cdf-binomial (electionResult year areaId politicianId) :size (sumResults (wardResults year areaId)) :prob (binParam year areaId politicianId)))

(defn formatted-surprise [year areaId politicianId] (if (< (*(sumResults (wardResults year areaId)) (binParam year areaId politicianId)) (electionResult year areaId politicianId)) (surprise year areaId politicianId) (- (surprise year areaId politicianId) 1)))

(defn expectedVotes [year areaId politicianId] {:raw (*(sumResults (wardResults year areaId)) (binParam year areaId politicianId)) :perc (* 100 (/ (*(sumResults (wardResults year areaId)) (binParam year areaId politicianId)) (sumResults (wardResults year areaId))))})
(defn actualVotes [year areaId politicianId ] {:raw (electionResult year areaId politicianId), :perc (* 100 (/ (electionResult year areaId politicianId) (sumResults (wardResults year areaId))))})




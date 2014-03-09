(ns stats-api.statistical-logic.expectedresult
  (:use [incanter core stats charts]
        [stats-api.data-layer.repository]))


(defn safeLastYear [year]
  (if (= (lastYear year) nil)
    {"year" (str year)}
    (lastYear year)
  )
)

(defn lastYearResults [year areaId]
  (wardResults
    (get (safeLastYear year) "year")
    areaId
  )
)

(defn inLastYearResult [year areaId partyName]
  (contains?
    (set (allPartiesInElection (get (safeLastYear year) "year") areaId))
    partyName
  )
)

(defn getVotesInPreviousElectionForParty [year areaId partyName]
  (if (inLastYearResult year areaId partyName)
    (sum (map (fn[x] (get x "numVotes")) (filter (fn[x] (= (get x "partyName") partyName)) (lastYearResults year areaId))))
    1
  )
)

(defn getTotalVotesForParty [partyName year areaId]
  {
    "partyName" partyName,
    "numVotes" (getVotesInPreviousElectionForParty year areaId partyName)
  }
)

(defn getVotesForSomeParties [year areaId]
  (map (fn[x] (getTotalVotesForParty x year areaId))
       (allPartiesInElection year areaId)
  )
)

(defn sumResults [results]
  (sum (map (fn[x] (get x "numVotes"))
            results)
  )
)

(defn scaleSingleResult [result scaleFactor]
  {
    "partyName" (get result "partyName")
    "numVotes" (/ (get result "numVotes") scaleFactor)
  }
)

(defn delta [year areaId]
  (if (isFirstYearInArea year areaId)
    (map (fn[x] {:partyName x :delta 0}) (allPartiesInElection year areaId))

  (getVotesForSomeParties year areaId)
  )
  )

(defn iterateDeltas [acc year]
  (map
    (fn[x] {
             :partyName (get x :partyName)
             :delta (+ (get x :delta)
                       (get (getTotalVotesForParty
                              (get x :partyName)
                              year
                              831)
                        "numVotes")
                       )
             })
    acc
  )
  )

(def delta (fn [year]
             (loop [cnt year acc (map (fn[x] {:partyName x :delta 0}) (allPartiesInElection year 831))]
               (if (isFirstYearInArea cnt 831)
                 acc
                 (recur (get (safeLastYear cnt) "year") (iterateDeltas acc cnt))
               )
             )
           )
  )


(defn scale [results]
  (map (fn[x] (scaleSingleResult x (sumResults results)))
       results
  )
)

(defn multivariate-parameters [year areaId] (scale (getVotesForSomeParties year areaId)))

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

(defn getTotalVotesForPartyInPreviousYear [partyName year areaId]
  {
    :partyName partyName,
    :numVotes (getVotesInPreviousElectionForParty year areaId partyName)
  }
)

(defn getTotalVotesInAllRegionsForPartyInPreviousYear [partyName year]
  {
    :partyName partyName,
    :numVotes (get (first (filter (fn[x] (= partyName (get x "partyName"))) (partyResults year))) "votes")
    }
  )

(defn getTotalVotesInAYearForPartiesServingAnArea [year areaId]
  (sum (set (map (fn[x] (get x "votes"))
                 (filter (fn[x] (contains? (set (allPartiesInElection year areaId)) (get x "partyName"))) (partyResults year))
                 )))
  )

(defn sumResults [results]
  (sum (map (fn[x] (get x :numVotes))
            results)
       )
  )

(defn getVotesForSomeParties [year areaId]
  (map (fn[x] (getTotalVotesForPartyInPreviousYear x year areaId))
       (allPartiesInElection year areaId)
       )
  )

(defn GlobalVoteScaledToLocal [year areaId]
  (map (fn[x]
         {
           :partyName x
           :numVotes (*
                       (/
                         (get (getTotalVotesInAllRegionsForPartyInPreviousYear x year) :numVotes)
                         (getTotalVotesInAYearForPartiesServingAnArea year areaId)
                       )

                       (sumResults (getVotesForSomeParties year areaId))
                     )
           }
         )
       (allPartiesInElection year areaId)
  ))

(defn GetThingWithPartyName [partyName array]
  (first (filter (fn[x] (= partyName (get x :partyName))) array))
  )

(defn deltaUpdater [year areaId]
  (map (fn[x]

         {:partyName x
          :numVotes (-
                      (get (GetThingWithPartyName x (GlobalVoteScaledToLocal year areaId)) :numVotes)
                      (get (GetThingWithPartyName x (getVotesForSomeParties year areaId)) :numVotes)
                    )
          }

         ) (allPartiesInElection year areaId))
  )

(defn scaleSingleResult [result scaleFactor]
  {
    "partyName" (get result :partyName)
    "numVotes" (/ (get result :numVotes) scaleFactor)
  }
)

(defn delta [year areaId]
  (if (isFirstYearInArea year areaId)
    (map (fn[x] {:partyName x :delta 0}) (allPartiesInElection year areaId))

  (getVotesForSomeParties year areaId)
  )
  )

(def decayRate 0.5)

(defn iterateDeltas [acc year]
  (map
    (fn[x] {
             :partyName (get x :partyName)
             :delta (+ (* (get x :delta) decayRate)
                       (get (GetThingWithPartyName (get x :partyName) (deltaUpdater
                              year                        `
                              831))
                        :numVotes)
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

(defn Deviation [year areaId partyName]
  (/
    (-

      (get (GetThingWithPartyName
           partyName
      (getVotesForSomeParties year areaId)
    ), :numVotes)

    (get (GetThingWithPartyName
           partyName
           (GlobalVoteScaledToLocal year areaId)
           ), :numVotes)
    )

    (get (GetThingWithPartyName
           partyName
           (GlobalVoteScaledToLocal year areaId)
           ), :numVotes)
  )

)

(defn scale [results]
  (map (fn[x] (scaleSingleResult x (sumResults results)))
       results
  )
)

(defn multivariate-parameters [year areaId] (scale (getVotesForSomeParties year areaId)))

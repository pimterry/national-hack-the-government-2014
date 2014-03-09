(ns stats-api.statistical-logic.expectedresult
  (:use [incanter core stats charts]
        [stats-api.data-layer.repository]))


(defn lastYearResults [year areaId] (wardResults (get (lastYear year) "year") areaId))

(defn inLastYearResult [year areaId partyName] (contains? (set (allPartiesInElection year areaId)) partyName))

(defn getVotesInPreviousElectionForParty [year areaId partyName] (if (inLastYearResult year areaId partyName) (sum (map (fn[x] (get x "numVotes")) (filter (fn[x] (= (get x "partyName") partyName)) (lastYearResults year areaId)))) 1))

(defn getTotalVotesForParty [partyName year areaId] {"partyName" partyName, "numVotes" (getVotesInPreviousElectionForParty year areaId partyName)})

(defn getVotesForSomeParties [year areaId] (map (fn[x] (getTotalVotesForParty x year areaId)) (allPartiesInElection year areaId)))

(defn sumResults [results] (sum (map (fn[x] (get x "numVotes")) results)))

(defn scaleSingleResult [result scaleFactor] {"partyName" (get result "partyName"), "numVotes" (/ (get result "numVotes") scaleFactor)})

(defn scale [results] (map (fn[x] (scaleSingleResult x (sumResults results))) results))

(defn multivariate-parameters [year areaId] (scale (getVotesForSomeParties year areaId)))

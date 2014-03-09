(ns stats-api.data-layer.repository
  (:use stats-api.data-layer.connection-config))

(defn years [] (run-query "MATCH (y:year) RETURN y.name AS name"))

(defn areas [] (run-query "MATCH (a:area) RETURN a.name AS name, id(a) AS id"))

(defn politician [] (run-query "MATCH (p:politician) RETURN p.name AS name, id(p) AS id"))

(defn parties [] (run-query "MATCH (p:party) RETURN p.name AS name"))

;;not exposed:

(defn electionResult [year, areaId, politicianId] (get (first (run-query (str "MATCH (p:politician)-[r:stood_in]->(e:election), (e:election)-[during]->(y:year), (e:election)-[held_in]->(a:area) WHERE y.name = \"" year "\" AND id(a)=" areaId  "AND id(p) =" politicianId " RETURN r.votes AS votes"))) "votes" ))

(defn extractPartyName [thingWithPartyName] (get thingWithPartyName "partyName"))

(defn allPartiesInElection [year, areaId] (map extractPartyName (run-query (str "MATCH (pa:party)<-[member_of]-(p:politician)-[r:stood_in]->(e:election)-[during]->(y:year), (e:election)-[held_in]->(a:area) WHERE y.name=\"" year "\" AND id(a)=" areaId " RETURN DISTINCT pa.name AS partyName"))))

(defn partyResults [year] (run-query (str "MATCH (pa:party)<-[member_of]-
                                        (p:politician)-[r:stood_in]->
                                        (e:election)-[during]->
                                        (y:year)
                                        WHERE y.name = \"" year "\"
                                        RETURN SUM(r.votes) as partyName AS votes, pa.name AS partyName")))

(defn  wardResults [year, areaId] (run-query (str "MATCH (pa:party)<-[member_of]-
                                                (p:politician)-[r:stood_in]->
                                                (e:election)-[held_in]->(a:area),
                                                (e:election)-[during]->(y:year)
                                                WHERE y.name=\"" year "\" AND id(a)=" areaId " RETURN id(p), pa.name AS partyName, SUM(r.votes) AS numVotes")))


(defn lastYear [year] (first (run-query (str "MATCH (y:year)-[p:previous]->(n:year) WHERE y.name=\"" year "\" RETURN n.name AS year"))))

(defn partyForPolitician [politicianId] (get (first (run-query (str "MATCH (p:politician)-[member_of]->(pa:party) WHERE id(p)=" politicianId "RETURN pa.name AS partyName ORDER BY pa.name"))) "partyName"))
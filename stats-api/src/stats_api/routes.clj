(ns stats-api.routes
  (:use compojure.core)
  (:use ring.middleware.json)
  (:use ring.middleware.cors)
  (:use stats-api.statistical-logic.surprise)
  (:use stats-api.statistical-logic.expectedresult)
  (:require [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [stats-api.data-layer.repository :as fetch]))

(defroutes api-routes
           ;;Homepage
           (GET "/" [] {:body "Poltician Surprise API Endpoint"})

           ;;End points to get all nodes of a particular type
           (GET "/years" [] {:body { :years (fetch/years)}})
           (GET "/areas" [] {:body { :areas (fetch/areas)}})
           (GET "/politicians" [] {:body { :politicians (fetch/politician)}})
           (GET "/parties" [] {:body { :parties (fetch/parties)}})

           ;;Surprise Endpoint
           (GET "/elections/:year/:areaId/:politicianId" [year areaId politicianId] {:body { :surprise (surprise year areaId politicianId)}})

           ;;404
           (route/not-found "Page not found"))

(defn -main []
  (let [port (Integer/parseInt (get (System/getenv) "PORT" "8080"))]
    (jetty/run-jetty
      (-> (wrap-json-response api-routes)
          (wrap-cors :access-control-allow-origin #".*"))
      {:port port})))

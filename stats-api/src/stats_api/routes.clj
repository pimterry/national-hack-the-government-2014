(ns stats-api.routes
  (:use compojure.core)
  (:use ring.middleware.json)
  (:use stats-api.statistical-logic.surprise)
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
           (GET "/elections/:year/:area/:politician" [year areaId politicianId] {:body { :surprise (surprise year areaId politicianId)}})

           ;;404
           (route/not-found "Page not found"))

(defn -main [] (jetty/run-jetty (wrap-json-response api-routes) {:port 8080}))
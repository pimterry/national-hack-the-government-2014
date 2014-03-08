(ns stats-api.routes
  (:use compojure.core)
  (:use ring.middleware.json)
  (:use stats-api.statistical-logic.surprise)
  (:require [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [stats-api.data-layer.repository :as fetch]))

(defroutes api-routes
           (GET "/" [] {:body "Hello Compojure!"})
           (GET "/years" [] {:body { :years (fetch/years)}})
           (GET "/areas" [] {:body { :areas (fetch/areas)}})
           (GET "/politicians" [] {:body { :people (fetch/politician)}})
           (GET "/elections/:year/:area/:politician" [year areaId politicianId] {:body { :surprise (surprise year areaId politicianId)}})
           (route/not-found "Page not found"))

(defn -main [] (jetty/run-jetty (wrap-json-response api-routes) {:port 8080}))
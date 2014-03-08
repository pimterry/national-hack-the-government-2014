(ns stats-api.routes
  (:use compojure.core)
  (:use ring.middleware.json)
  (:require [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [stats-api.repository :as fetch]))

(defroutes api-routes
           (GET "/" [] {:body "Hello Compojure!"})
           (GET "/years" [] {:body { :years (fetch/years)}})
           (GET "/areas" [] {:body { :areas (fetch/areas)}})
           (route/not-found "Page not found"))

(defn -main [] (jetty/run-jetty (wrap-json-response api-routes) {:port 8080}))
(ns stats-api.routes
  (:use compojure.core)
  (:use ring.middleware.json)
  (:use stats-api.repository)
  (:require [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            ))

(defroutes helloworld-routes
           (GET "/" [] {:body "Hello Compojure!"})
           (GET "/years" [] {:body { :years (fetch-years)}})
           (route/not-found "Page not found"))

(defn -main [] (jetty/run-jetty (wrap-json-response helloworld-routes) {:port 8080}))
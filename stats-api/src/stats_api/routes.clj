(ns stats-api.routes
  (:use compojure.core)
  (:require [compojure.route :as route]
            [ring.adapter.jetty :as jetty]))

(defroutes helloworld-routes
           (GET "/" [] {:body "Hello Compojure!"})
           (GET "/:id" [id] {:body id})
           (route/not-found "Page not found"))

(defn boot-server []
  (jetty/run-jetty helloworld-routes {:port 8080}))


(ns simple-mmo-server.endpoint.example
  (:require [compojure.core :refer :all]
            [clojure.java.io :as io]))

(defn example-endpoint [config]
  (context "/example" []
    (GET "/" []
      (io/resource "simple_mmo_server/endpoint/example/example.html"))))

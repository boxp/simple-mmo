(ns simple-mmo-server.system
  (:require [clojure.java.io :as io]
            [clojure.tools.reader.edn :as edn]
            [com.stuartsierra.component :as component]
            [duct.component.endpoint :refer [endpoint-component]]
            [duct.component.handler :refer [handler-component]]
            [duct.middleware.not-found :refer [wrap-not-found]]
            [duct.middleware.route-aliases :refer [wrap-route-aliases]]
            [meta-merge.core :refer [meta-merge]]
            [ring.component.jetty :refer [jetty-server]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.webjars :refer [wrap-webjars]]
            [buddy.auth :refer [authenticated?]]
            [buddy.auth.accessrules :refer [wrap-access-rules]]
            [buddy.auth.backends.session :refer [wrap-authentication
                                                 wrap-authorization]]
            [simple-mmo-server.endpoint.example :refer [example-endpoint]]
            [simple-mmo-server.component.database :refer [db]]))

(def buddy-rules
  [{:pattern #"^/{login|logout|register}/.*" :handler authenticated?}])

(def base-config
  {:app {:middleware [[wrap-not-found :not-found]
                      [wrap-webjars]
                      [wrap-defaults :defaults]
                      [wrap-route-aliases :aliases]
                      [wrap-access-rules {:rules buddy-rules :policy :reject}]]
         :not-found  (io/resource "simple_mmo_server/errors/404.html")
         :defaults   (meta-merge site-defaults {:static {:resources "simple_mmo_server/public"}})
         :aliases    {"/" "/index.html"}}})

(defn new-system [config]
  (let [config (meta-merge base-config config)]
    (-> (component/system-map
         :app  (handler-component (:app config))
         :db (-> "db/simple-mmo-db.clj" 
                 io/resource 
                 slurp 
                 edn/read-string 
                 db))
         :http (jetty-server (:http config))
         :example (endpoint-component example-endpoint))
        (component/system-using
         {:http [:app]
          :app  [:db :example]
          :db []
          :example []}))))

(ns simple-mmo-server.component.database
  (:require [com.stuartsierra.component :as component]))

(defrecord Database [db-spec connection]
  component/Lifecycle
  (start [this]
    (if (:connection this)
      this
      (assoc this :connection db-spec)))
  (stop [this]
    (if (:connection this)
      (assoc this :connection nil)
      this)))

(defn db 
  [db-spec]
  (map->Database {:db-spec db-spec
                  :connection nil}))

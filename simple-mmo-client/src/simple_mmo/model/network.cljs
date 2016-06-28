(ns simple-mmo.model.network
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [simple-mmo.model.core :refer [game-state]]
            [simple-mmo.model.actor :refer [spawn! merge-actor!]]
            [simple-mmo.model.bunny :as bunny]
            [simple-mmo.model.dialog :refer [info! post!]]
            [cljs.core.async :refer [chan put! <! timeout]]
            [peer-cljs.core :as peer]))

(def peer (js/Peer. #js{:host "peer.boxp.tk"
                        :port 80
                        :path "/"}))

(defn ->json
  [type coll]
  (-> coll
      (assoc :type type)
      (dissoc :obj)
      clj->js
      js/JSON.stringify))

(defn ->coll
  [s]
  (-> s
      js/JSON.parse
      (js->clj :keywordize-keys true)))

(defn connect
  [id]
  (.connect peer id))

(defn conn->data-obs
  "Convert connection to Observable data stream"
  [conn]
   (Rx.Observable.fromEvent conn "data"))

(defn connection-obs
  "Connection stream"
  []
  (Rx.Observable.fromEvent peer "connection"))

(defn send
  [conn type coll]
  (->> coll
      (->json type)
      (.open conn)))

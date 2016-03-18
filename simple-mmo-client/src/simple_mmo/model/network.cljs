(ns simple-mmo.model.network
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [simple-mmo.model.core :refer [game-state]]
            [simple-mmo.model.actor :refer [spawn! merge-actor!]]
            [simple-mmo.model.bunny :as bunny]
            [simple-mmo.model.dialog :refer [info! post!]]
            [cljs.core.async :refer [chan put! <! timeout]]
            [peer-cljs.core :as peer]))

(defn- ->json
  [coll type]
  (-> coll
      (assoc :type type)
      (dissoc :obj)
      clj->js
      js/JSON.stringify))

(defn- ->coll
  [s]
  (-> s
      js/JSON.parse
      (js->clj :keywordize-keys true)))

(defn- on-receive-actor
  [actor]
  (let [a (get (:actors @game-state) (:name actor))]
    (if a
        (swap! game-state assoc-in [:actors (:name actor)]
               (merge-actor! a actor))
        (cond
          ;; bunny
          :else
          (swap! game-state update :actors
            #(assoc % (:name actor)
               (-> (bunny/bunny (:name actor)
                                (:x actor) (:y actor)
                                (:rotation actor))
                   spawn!)))))))

(defn send!
  [conn data type]
  (peer/send! conn (->json data type)))

(defn send-all!
  [data type]
  (doseq [c (:connects @game-state)]
    (peer/send! c (->json data type)))
  data)

(defn data-loop 
  [conn]
  (go-loop []
    (let [data (->coll (<! (peer/receive conn)))]
      (case (:type data)
        "log" (post! (:txt data))
        "actor" (on-receive-actor data))
      (recur))))

(defn connection-loop []
  (go-loop [conn (<! (peer/accept (:peer @game-state)))]
    (println "Connect!" conn)
    ;; start data-loop
    (data-loop conn)
    ;; send current main-actor
    (send! conn (:main-actor @game-state) "actor")
    ;; add connection
    (swap! game-state update :connects #(conj % conn))
    (recur (<! (peer/accept (:peer @game-state))))))

(defn init-network []
  (go (swap! game-state assoc :peer
             (<! (peer/peer "peer.boxp.tk" 80 "/")))
      (info! (str "ID is "(get-in @game-state [:peer :id])))
      (connection-loop)))

(defn connect
  [id]
  (go (let [conn (<! (peer/connect (:peer @game-state) id))]
        ;; send current main-actor
        (send! conn (:main-actor @game-state) "actor")
        ;; start data-loop
        (data-loop conn)
        ;; add connection
        (swap! game-state update :connects #(conj % conn)))))

(defonce network-id (init-network))

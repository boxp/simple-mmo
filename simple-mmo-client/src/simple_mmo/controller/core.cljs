(ns ^:figwheel-no-load simple-mmo.controller.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :refer [chan put! timeout]]
            [clojure.set :refer [subset?]]
            [dommy.core :as dommy :refer-macros [sel1 sel]]
            [simple-mmo.model.core :refer [game-state]]
            [simple-mmo.model.actor :refer [change-state]]
            [simple-mmo.model.bunny :refer [move!]]
            [simple-mmo.model.dialog :refer [log post!]]
            [simple-mmo.view.core :refer [renderer]]
            [simple-mmo.model.network :refer [connect
                                              send-all!]]))

(def A 65)
(def S 83)
(def D 68)
(def W 87)
(def T 84)
(def C 67)

(def right (/ js/Math.PI 2))
(def left (* js/Math.PI 1.5))
(def up 0)
(def down js/Math.PI)

(def right-up (/ js/Math.PI 4))
(def right-down (* js/Math.PI 0.75))
(def left-down (* js/Math.PI 1.25))
(def left-up (* js/Math.PI 1.75))

(defonce controller-state (atom {:pressed #{}}))

(defn- send-log! []
  (let [log (post! (str (get-in @game-state [:main-actor :name])
                        ": "
                        (js/prompt "")))]
    (send-all! log "log")))

(defn- on-keydown
  [e]
  (let [kc (.-keyCode e)]
    (cond 
      (= kc T)
      (send-log!)
      (= kc C)
      (connect (js/prompt "idを入力してください"))
      :else
      (swap! controller-state update :pressed
             conj kc))))

(defn- on-keyup
  [e]
  (let [kc (.-keyCode e)]
    (swap! controller-state update :pressed
           disj kc)))

(defn- move-main-actor!
  [rotation]
  (let [actor (-> @game-state :main-actor 
                  (move! rotation)
                  (change-state :move))]
    (send-all! actor "actor")
    (swap! game-state assoc :main-actor actor)))

(defn- stop-main-actor!  []
  (let [actor (-> @game-state :main-actor
                  (change-state :wait))]
    (swap! game-state assoc :main-actor actor)))

(defonce controller-loop
  (go-loop []
    (let [pressed (:pressed @controller-state)]
      (cond
        ;; right-up
        (subset? #{W D} pressed)
        (move-main-actor! right-up)
        ;; right-down
        (subset? #{D S} pressed)
        (move-main-actor! right-down)
        ;; left-down
        (subset? #{A S} pressed)
        (move-main-actor! left-down)
        ;; left-up
        (subset? #{A W} pressed)
        (move-main-actor! left-up)
        ;; up
        (contains? pressed W)
        (move-main-actor! up)
        ;; right
        (contains? pressed D)
        (move-main-actor! right)
        ;; down
        (contains? pressed S)
        (move-main-actor! down)
        ;; left
        (contains? pressed A)
        (move-main-actor! left)
        :else (stop-main-actor!))
      (<! (timeout 10))
      (recur))))

(dommy/unlisten! (sel1 :body) :keydown on-keydown)
(dommy/listen! (sel1 :body) :keydown on-keydown)
(dommy/unlisten! (sel1 :body) :keyup on-keyup)
(dommy/listen! (sel1 :body) :keyup on-keyup)

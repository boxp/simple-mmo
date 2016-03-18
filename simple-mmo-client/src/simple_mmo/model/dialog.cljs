(ns simple-mmo.model.dialog
  (:require [simple-mmo.view.core :refer [overlay height]]
            [simple-mmo.view.text :refer [text]]
            [simple-mmo.model.core :refer [game-state]]
            [pixi-cljs.util :refer [update-prop! set-prop!]]
            [clojure.set :refer [difference]]))

(def font-size 20)

(def colors
  #{"#96514d" "#e6b422" "#006e54"
    "#895b8a" "#8d6449" "#d9a62e"
    "#00a381" "#824880" "#deb068"
    "#d3a243" "#38b48b" "#915c8b"})

(defrecord Log [txt color obj size])

(defn- select-color
  [used-colors]
  (rand-nth (vec (difference colors used-colors))))

(defn log
  [txt logs]
  (let [color (->> (map :color logs)
                  set 
                  select-color)]
    (->Log txt color (text txt color font-size) font-size)))

(defn- shift-log!
  [log]
  (-> log :obj
      (update-prop! [:y] (fn [y] (- y font-size)))))

(defn post!
  [txt]
  (let [logs (:logs @game-state)
        log (log txt logs)]
    (doall (map shift-log! logs))
    (set-prop! (:obj log) [:y] (- height font-size 5))
    (.addChild overlay (:obj log))
    (swap! game-state update :logs #(conj logs log))
    log))

(defn info!
  [txt]
  (post! (str "info: " txt)))

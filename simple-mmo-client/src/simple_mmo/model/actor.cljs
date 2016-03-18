(ns simple-mmo.model.actor
  (:require [pixi-cljs.util :refer [get-prop set-prop!]]
            [simple-mmo.view.core :refer [stage]]))

(def status
  #{:attack :move :damage :die :talk :wait})

(defrecord Actor [name x y width height hp 
                  rotation status graphics
                  obj speed])

(defn change-state
  [actor state]
  (if (status state)
    (assoc actor :status state)
    actor))

(defn collise?
  [actor1 actor2]
  (let [dw (/ (:width actor1) 2)
        dh (/ (:height actor1) 2)
        x1 (- (:x actor1) dw)
        y1 (+ (:y actor1) dh)
        x2 (+ (:x actor1) dw)
        y2 (- (:y actor1) dh)]
    (and
      (and (<= (:x actor2) x2) (>= (:x actor2) x1))
      (and (<= (:y actor2) y1) (>= (:y actor2) y2)))))

(defn focus-main-actor!
  [renderer container actor]
  (let [x (- (/ (get-prop renderer [:width]) 2)
             (get-prop actor [:x]))
        y (- (/ (get-prop renderer [:height]) 2)
             (get-prop actor [:y]))]
    (set-prop! container [:position :x] x)
    (set-prop! container [:position :y] y)))

(defn spawn!
  [actor]
  (set-prop! (:obj actor) [:x] (:x actor))
  (set-prop! (:obj actor) [:y] (:y actor))
  (set-prop! (:obj actor) [:anchor :x] 0.5)
  (set-prop! (:obj actor) [:anchor :y] 0.5)
  (set-prop! (:obj actor) [:rotation] (:rotation actor))
  (.addChild stage (:obj actor))
  actor)

(defn merge-actor!
  [actor1 actor2]
  (set-prop! (:obj actor1) [:x] (:x actor2))
  (set-prop! (:obj actor1) [:y] (:y actor2))
  (set-prop! (:obj actor1) [:rotation] (:rotation actor2))
  (set-prop! (:obj actor1) [:hp] (:hp actor2))
  (set-prop! (:obj actor1) [:status] (:status actor2))
  (set-prop! (:obj actor1) [:speed] (:speed actor2))
  (-> actor1
      (assoc :x (:x actor2))
      (assoc :y (:y actor2))
      (assoc :rotation (:rotation actor2))
      (assoc :hp (:hp actor2))
      (assoc :status (:status actor2))
      (assoc :speed (:speed actor2))))

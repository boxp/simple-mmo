(ns simple-mmo.model.bunny
  (:require [pixi-cljs.util :refer [set-prop!]]
            [simple-mmo.view.core :refer [stage]]
            [simple-mmo.view.sprite :refer [sprite]]
            [simple-mmo.model.actor :refer [->Actor change-state
                                            focus-main-actor!]]))

(defn bunny
  [name x y rotation]
  (->Actor name x y 26 37 100
           0 nil "bunny"
           (sprite "images/bunny.png") 5))

(defn move!
  [bunny direction]
  (let [x (+ (:x bunny) (* (js/Math.sin direction) (:speed bunny)))
        y (- (:y bunny) (* (js/Math.cos direction) (:speed bunny)))]
    (set-prop! (:obj bunny) [:rotation] direction)
    (set-prop! (:obj bunny) [:x] x)
    (set-prop! (:obj bunny) [:y] y)
    (-> bunny
      (assoc :x x)
      (assoc :y y)
      (assoc :rotation direction))))

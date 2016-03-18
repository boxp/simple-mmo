(ns simple-mmo.view.sprite
  (:require [pixi-cljs.util :refer [get-prop set-prop!]]))

(defn sprite
  [name]
  (js/PIXI.Sprite.fromImage name))

(ns simple-mmo.view.sprite)

(defn sprite
  [name]
  (js/PIXI.Sprite.fromImage name))

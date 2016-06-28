(ns simple-mmo.view.core
  (:require [dommy.core :as dommy :refer-macros [sel1 sel]]))

(def width 640)
(def height 360)

(defonce renderer (js/PIXI.autoDetectRenderer width height))

(defonce container (js/PIXI.Container.))

(defonce stage (js/PIXI.Container.))

(defonce overlay (js/PIXI.Container.))

(defonce frame-listener (atom #()))

(.addChild container stage)
(.addChild container overlay)

(defonce anim-id
  (pixi/show! renderer container frame-listener))

(defn on-resize []
  (let [w (.-innerWidth js/window)
        h (.-innerHeight js/window)
        ratio-width (/ w width)
        ratio-height (/ h height)
        ratio (if (< ratio-width ratio-height)
                ratio-width ratio-height)]
    (set! (.. renderer -view -style width) 
          (str (* width ratio) "px"))
    (set! (.. renderer -view -style -height) 
          (str (* height ratio) "px"))))

(dommy/listen! js/window :resize on-resize)
(on-resize)

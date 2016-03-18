(ns simple-mmo.view.core
  (:require [dommy.core :as dommy :refer-macros [sel1 sel]]
            [pixi-cljs.core :as pixi]
            [pixi-cljs.util :refer [get-prop update-prop! set-prop!]]))

(def width 640)
(def height 360)

(defonce renderer (pixi/renderer width height))

(defonce container (pixi/container))

(defonce stage (pixi/container))

(defonce overlay (pixi/container))

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
    (set-prop! renderer [:view :style :width] 
               (str (* width ratio) "px"))
    (set-prop! renderer [:view :style :height] 
               (str (* height ratio) "px"))))

(dommy/listen! js/window :resize on-resize)
(on-resize)

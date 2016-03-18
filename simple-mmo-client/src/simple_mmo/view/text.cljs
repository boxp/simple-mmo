(ns simple-mmo.view.text)

(defn text
  [txt color size]
  (js/PIXI.Text. txt #js {:font (str size "px Arial")
                          :fill color 
                          :align "left"}))

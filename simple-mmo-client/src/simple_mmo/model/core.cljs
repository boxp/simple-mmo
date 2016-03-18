(ns simple-mmo.model.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [simple-mmo.model.bunny :as bunny]
            [simple-mmo.model.actor :refer [focus-main-actor! spawn!]]
            [simple-mmo.view.core :refer [renderer stage frame-listener
                                          width height]]))

(defonce game-state 
  (atom {:main-actor (-> (bunny/bunny (js/prompt "プレイヤー名を入力して下さい") 0 0 0)
                         spawn!)
         :peer nil
         :logs []
         :objects []
         :connects []
         :actors {}}))
       
(reset! frame-listener
  (fn []
    (focus-main-actor! renderer
                       stage
                       (:main-actor @game-state))))

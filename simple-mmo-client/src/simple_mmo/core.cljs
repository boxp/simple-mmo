(ns simple-mmo.core
  (:require [simple-mmo.model.core]
            [simple-mmo.view.core]
            [simple-mmo.controller.core]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

(ns roam-timeline.core
  (:require [roam-timeline.roam :as roam]
            [oz.core :as oz]))


(defn datafy [r]
  r)

(defn display [data]
  (oz/view!
   {:width 600 :height 400
    :data {:values data}}
   ))

(defn -main
  [zip-path]
  (-> zip-path
      roam/read-roam-json-zip
      datafy
      display))

(ns roam-timeline.core
  (:require [roam-timeline.roam :as roam]
            [roam-timeline.utils :as u]
            [oz.core :as oz]))

(defn display
  [data]
  (oz/view!
   {:width 600 :height 400
    :data {:values data}
    :mark {:type "line" :tooltip {:content "data"} :point true} 
    :transform [{:calculate "time(datum.createTime)" :as "time"}
                {:calculate "date(datum.createTime)" :as "date"}]
    :encoding {:x {:field "date"
                   :type "temporal"}
               :y {:field "time"
                   :type "temporal"}
               :color {:field "tag"
                       :type "nominal"}}
    }
   ))

(defn block-seq
  [raw]
  (mapcat
   #(tree-seq (fn [_] true) :children %)
   raw))

(defn tag-block [{:keys [string] :as block}]
  (assoc block :tags (and string (re-seq #"\#\w+" string))))

;;; Unfortunately, there is no way pick out Daily Notes pages (except by parseing title as date)
;;; First thought: pull out tags. Graph timelines day vs time.
;;; Could use create-time/edit time to make bars but that is probably not what you want?

;(def zip-path "/Users/mtravers/Downloads/Roam-Export-1609638443921.zip")

(defn -main
  [zip-path]
  (->> zip-path
      roam/read-roam-json-zip
      block-seq
      (map tag-block)
;      (u/group-by-multiple :tags)
      ;; flatten
      (mapcat (fn [block] (map (fn [tag] (assoc block :tag tag)) (:tags block))))
      display))

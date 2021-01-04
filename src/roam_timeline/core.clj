(ns roam-timeline.core
  (:require [roam-timeline.roam :as roam]
            [roam-timeline.utils :as u]
            [oz.core :as oz]))

(def msec-per-day (* 1000 60 60 24)) ; 86400000

(defn display
  [data]
  (oz/view!
   {:width 600 :height 400
    :data {:values data}
    :mark {:type "line" :tooltip {:content "data"} :point true} 
    :transform [{:calculate "datum.createTime % 86400000" :as "time"}
                {:calculate "datum.createTime / 86400000" :as "date"}]
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

(defn tag-block [{:keys [string editTime] :as block}]
  (let [time (java.util.Date. editTime)]
    (assoc block
           :tags (and string (re-seq #"\#\w+" string))
           :editTimeString (and editTime (str (java.util.Date. editTime))))))

;;; Unfortunately, there is no way pick out Daily Notes pages (except by parsing title as date)
;;; First thought: pull out tags. Graph timelines day vs time.
;;; Could use create-time/edit time to make bars but that is probably not what you want?

;(def zip-path "/Users/mtravers/Downloads/Roam-Export-1609638443921.zip")

(defn filter-to-real-tags [blocks]
  (let [tags
        (set
         (map first
              (filter (fn [[key blocks]] (>= (count blocks) 3))
                      (group-by :tag blocks))))]
    (prn :tags tags)
    (filter (comp tags :tag) blocks)))

(defn -main
  [zip-path]
  (->> zip-path
      roam/read-roam-json-zip
      block-seq
      (map tag-block)
      (mapcat (fn [block] (map (fn [tag] (assoc block :tag tag)) (:tags block))))
      filter-to-real-tags
      display))

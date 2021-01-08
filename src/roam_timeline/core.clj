(ns roam-timeline.core
  (:require [roam-timeline.roam :as roam]
            [roam-timeline.utils :as u]
            [oz.core :as oz]))


#_
(defn block-seq
  [raw]
  (mapcat
   #(tree-seq (fn [_] true) :children %)
   raw))

(defn block-seq
  [raw]
  (mapcat
   (fn [page]
     (tree-seq (fn [_] true)
               (fn [block]
                 (map #(assoc % :page (:title page))
                      (:children block)))
               page))
   raw))

(defn string-tags
  [s]
  (and s
       (remove (partial re-matches #"[0-9a-fA-F]+") ;ignore hex strings which are block refs
               (concat (map second (re-seq #"\#(\w+)" s))
                       ;; TODO make this optional
                       (map second (re-seq #"\[\[(\w+)\]\]" s))))))

(defn tag-block
  [{:keys [string] :as block}]
  (assoc block :tags (string-tags string)))

(defn humanize-times
  [{:keys [editTime createTime] :as block}]
  (assoc block
         :editTimeString (and editTime (str (java.util.Date. editTime)))
         :createTimeString (and createTime (str (java.util.Date. createTime)))))

(def daily-log-regex #"(?:January|February|March|April|May|June|July|August|September|October|November|December) \d+.., \d+")

(defn daily-log?
  [block]
  (re-matches daily-log-regex (or (:page block) (:title block))))
  

;;; First thought: pull out tags. Graph timelines day vs time.
;;; Could use create-time/edit time to make bars but that is probably not what you want?



(defn filter-to-real-tags
  [blocks]
  (let [tags
        (set
         (map first
              (filter (fn [[key blocks]] (>= (count blocks) 3))
                      (group-by :tag blocks))))]
    (prn :tags tags)
    (filter (comp tags :tag) blocks)))

(def msec-per-day (* 1000 60 60 24)) ; 86400000
(def msec-per-year (* 360 msec-per-day))

(defn filter-to-year [blocks]
  (let [latest (reduce max (map :editTime blocks)) ;not everything has a :createTime
        start (- latest msec-per-year)]
    (filter #(> (:editTime %) start) blocks)))

(defn display
  [data]
  (oz/view!
   {:width 600 :height 400
    :data {:values data}
    :mark {:type "line"  :point true} 
    :transform [{:calculate "(datum.createTime % 86400000) / 3600000" :as "time"}
                {:calculate "datum.createTime / 86400000" :as "date"}]
    :encoding {:x {:field "date"
                   :type "temporal"}
               :y {:field "time"
                   :type "ordinal"}
               :color {:field "tag"
                       :type "nominal"}
               :tooltip [{:field "tag"}
                         {:field "string"}
                         {:field "createTimeString"}]}
    }
   ))

;; Try a different graph style: x is day, y is count
(defn display2
  [data]
  (oz/view!
   {:width 1000 :height 400
    :data {:values data}
    :mark {:type "bar"  :point true} 
    :encoding {:x {:field "createTime"
                   :timeUnit "yearmonthdate"
                   :type "temporal"}
               :y {:aggregate "count"
                   :type "quantitative"}
               :color {:field "tag"
                       :type "nominal"}
               :tooltip [{:field "tag"}
                         {:field "string"}
                         {:field "createTimeString"}]}
    }
   ))

;(def zip-path "/Users/mtravers/Downloads/Roam-Export-1609638443921.zip")

(defn zip->blocks [zip-path]
  (->> zip-path
      roam/read-roam-json-zip
      block-seq
      (map tag-block)))

(defn -main
  [zip-path]
  (->> zip-path
      roam/read-roam-json-zip
      block-seq
      filter-to-year
      (map tag-block)
;      (filter daily-log?)
      (mapcat (fn [block] (map (fn [tag] (assoc block :tag tag)) (:tags block))))
      filter-to-real-tags
      (map humanize-times)
      display2))

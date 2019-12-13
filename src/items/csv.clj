(ns items.csv
  (:require
    [java-time :as jt]
    [duct.logger :refer [log]]
    [items.system :refer [logger items-db json-path]]
    [clj-bom.core :as bom]
    [clojure.data.csv :as csv]
    [shun.core :refer [coll-map select-vals]]))

(def time-format "YYYY/MM/dd HH:mm:ss")
(def date-format "YYYY/MM/dd")

(defn type-transform [field]
  (cond
    (jt/local-date-time? field) (jt/format time-format field)
    (jt/local-date? field) (jt/format date-format field)
    :else field))

(defn ->csv-row [m kv]
  (coll-map (fn [k m]
              (->> (get m k)
                   type-transform))
            kv
            m))

(defn map->csv-vec
  ([map-v kv title?]
   (let [title (map name kv)
         csv-vec (map #(->csv-row % kv) map-v)]
     (if title?
       (cons title csv-vec)
       csv-vec)))
  ([map-v kv]
   (map->csv-vec map-v kv false)))

(defn vec->csv [path v]
  (try
    (with-open [writer (bom/bom-writer "UTF-8" path)]
      (csv/write-csv writer v)
      (log (logger) :info "vec->csv success:" path))
    (catch Exception ex
      (log (logger) :error "vec->csv error:" path)
      (log (logger) :error  (.getMessage ex)))))

(defn map->csv
  ([path m kv title?]
   (->> (map->csv-vec m kv title?)
        (vec->csv path)))
  ([path m kv]
   (map->csv path m kv false)))

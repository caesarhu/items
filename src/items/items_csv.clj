(ns items.items-csv
  (:require
    [items.system :refer [logger items-db csv-path]]
    [items.boundary.db :as db]
    [java-time :as jt]
    [items.items-query :as query]
    [items.json-spec :refer [items-db-fields detail-fields stat-fields]]
    [items.csv :refer [map->csv]]
    [shun.core :refer [coll-map select-vals]]))

(defn filter-unit [unit coll]
  (let [kv [:單位 :子單位]
        unit-v (filter some? (select-vals unit kv))
        length (count unit-v)
        is-unit? (fn [m]
                   (= unit-v (take length (select-vals m kv))))]
    (filter is-unit? coll)))

(defn generate-unit-name
  ([start-date end-date unit]
   (let [raw-name (if unit
                    (apply str (select-vals unit [:單位 :子單位]))
                    "全局")
         date-fn #(jt/format (jt/formatter :iso-date) %)]
     (str raw-name "-" (date-fn start-date) "-to-" (date-fn end-date) ".csv")))
  ([start-date end-date]
   (generate-unit-name start-date end-date nil)))

(defn generate-csv-name
  ([start-date end-date kind-str unit]
   (str (csv-path) kind-str (generate-unit-name start-date end-date unit)))
  ([start-date end-date kind-str]
   (generate-csv-name start-date end-date kind-str nil)))

(defn generate-stats-name
  ([start-date end-date unit]
   (generate-csv-name start-date end-date "/統計-" unit))
  ([start-date end-date]
   (generate-stats-name start-date end-date nil)))

(defn generate-detail-name
  ([start-date end-date unit]
   (generate-csv-name start-date end-date "/明細-" unit))
  ([start-date end-date]
   (generate-detail-name start-date end-date nil)))

(defn generate-stats-csv
  ([start-date end-date]
   (let [stat (query/get-items-stat start-date end-date)
         whole-name (generate-stats-name start-date end-date)
         units (db/units (items-db))]
     (map->csv whole-name stat stat-fields true)
     (for [unit units]
       (let [unit-name (generate-stats-name start-date end-date unit)
             unit-stat (filter-unit unit stat)]
         (map->csv unit-name unit-stat stat-fields true)))))
  ([one-date]
   (generate-stats-csv one-date one-date))
  ([]
   (generate-stats-csv (jt/local-date 1 1 1) (jt/local-date 9999 9 9))))

(defn generate-detail-csv
  ([start-date end-date]
   (let [detail (query/query-items-period-record start-date end-date)
         whole-name (generate-detail-name start-date end-date)
         units (db/units (items-db))]
     (map->csv whole-name detail detail-fields true)
     (for [unit units]
       (let [unit-detail (filter-unit unit detail)
             unit-name (generate-detail-name start-date end-date unit)]
         (map->csv unit-name unit-detail detail-fields true)))))
  ([one-date]
   (generate-detail-csv one-date one-date))
  ([]
   (generate-detail-csv (jt/local-date 1 1 1) (jt/local-date 9999 9 9))))



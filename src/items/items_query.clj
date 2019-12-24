(ns items.items-query
  (:require
    [java-time :as jt :refer [local-date local-date-time]]
    [items.system :refer [db-call]]
    [items.boundary.db :as db]))

(defn query-items-period-record
  ([start-date end-date]
   (let [query (db-call db/items-period-record {:start-date  start-date :end-date (jt/plus end-date (jt/days 1))})]
     query))
  ([one-date]
   (query-items-period-record one-date one-date))
  ([]
   (query-items-period-record (local-date 1 1 1) (local-date 9999 9 9))))

(defn get-items-stat
  ([start-date end-date]
   (let [raw-stat (db-call db/stats-all {:start-date start-date :end-date (jt/plus end-date (jt/days 1))})
         stat (map #(assoc % :開始日期 start-date :結束日期 end-date) raw-stat)
         switch-apb (fn [stat-map]
                      (if-let [單位 (:單位 stat-map)]
                        stat-map
                        (assoc stat-map :單位 "全局")))]
     (map switch-apb stat)))
  ([one-date]
   (get-items-stat one-date one-date))
  ([]
   (get-items-stat (local-date 1 1 1) (local-date 9999 9 9))))

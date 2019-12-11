(ns items.boundary.db
  (:require [hugsql.core :as hugsql]
            [items.system :refer [logger items-db]]
            [duct.logger :refer [log]]
            duct.database.sql)
  (:import java.sql.SQLException))

(hugsql/def-db-fns "sql/queries.sql")

(defprotocol QueryDatabase
  (units [db])
  (users [db]))

(extend-protocol QueryDatabase
  duct.database.sql.Boundary
  (units [{db :spec}]
    (try
      (get-units db)
      (catch SQLException ex
        (log (logger) :error (format "Can not query units due to %s" (.getMessage ex))))))
  (users [{db :spec}]
    (try
      (get-users db)
      (catch SQLException ex
        (log (logger) :error (format "Can not query units due to %s" (.getMessage ex)))))))

(defprotocol InsertDatabase
  (insert-table-record [db table record]))

(extend-protocol InsertDatabase
  duct.database.sql.Boundary
  (insert-table-record [{db :spec} table record]
    (try
      (let [column-names (map name (keys record))
            column-vals (vals record)
            result (insert-table! db {:table table :column-names column-names :column-vals column-vals})]
        (if-let [id (val (ffirst result))]
          {:id id}
          (log (logger) :error ["Failed to add record." table record])))
      (catch SQLException ex
        (log (logger) :error (format "Record not added due to %s" (.getMessage ex)))))))

(defprotocol ItemsDatabase
  (items-period-record [db start-date end-date])
  (items-by-id [id])
  (stats-all [db start-date end-date]))

(extend-protocol ItemsDatabase
  duct.database.sql.Boundary
  (items-period-record [{db :spec} start-date end-date]
    (try
      (get-items-period-record db {:start-date start-date :end-date end-date})
      (catch SQLException ex
        (log (logger) :error (format "Query failed due to %s" (.getMessage ex))))))
  (items-by-id [id]
    (try
      (get-items-by-id db {:id id})
      (catch SQLException ex
        (log (logger) :error (format "Query failed due to %s" (.getMessage ex))))))
  (stats-all [{db :spec} start-date end-date]
    (try
      (get-stats-all db {:start-date start-date :end-date end-date})
      (catch SQLException ex
        (log (logger) :error (format "Query failed due to %s" (.getMessage ex)))))))
(ns items.boundary.db
  (:require [hugsql.core :as hugsql]
            [items.system :refer [logger items-db]]
            [duct.logger :refer [log]]
            [clojure.spec.alpha :as s]
            [items.specs :as specs]
            [items.boundary.coerce]
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
  (insert-table-record [db m]))

(extend-protocol InsertDatabase
  duct.database.sql.Boundary
  (insert-table-record [{db :spec} m]
    (try
      (let [result (insert-table! db m)]
        (if-let [id (val (ffirst result))]
          {:id id}
          (log (logger) :error ["Failed to add record." m])))
      (catch SQLException ex
        (log (logger) :error (format "Record not added due to %s" (.getMessage ex)))))))

(defprotocol ItemsDatabase
  (items-period-record [db m])
  (items-by-id [db m])
  (stats-all [db m]))

(extend-protocol ItemsDatabase
  duct.database.sql.Boundary
  (items-period-record [{db :spec} m]
    (try
      (get-items-period-record db m)
      (catch SQLException ex
        (log (logger) :error (format "Query failed due to %s" (.getMessage ex))))))
  (items-by-id [{db :spec} m]
    (try
      (get-items-by-id db m)
      (catch SQLException ex
        (log (logger) :error (format "Query failed due to %s" (.getMessage ex))))))
  (stats-all [{db :spec} m]
    (try
      (get-stats-all db m)
      (catch SQLException ex
        (log (logger) :error (format "Query failed due to %s" (.getMessage ex)))))))

;;; db spec

(s/fdef units
  :args (s/cat :db ::specs/boundary)
  :ret (s/coll-of map?))

(s/fdef users
  :args (s/cat :db ::specs/boundary)
  :ret (s/coll-of map?))

(s/fdef insert-table-record
  :args (s/cat :db ::specs/boundary
               :record-map (s/keys :req-un [::specs/table ::specs/column-names ::specs/column-vals]))
  :ret (s/keys :req-un [::specs/id]))

(s/fdef items-period-record
  :args (s/cat :db ::specs/boundary
               :date-map (s/keys :req-un [::specs/start-date ::specs/end-date]))
  :ret (s/coll-of map?))

(s/fdef items-by-id
  :args (s/cat :db ::specs/boundary
               :id-map (s/keys :req-un [::specs/id]))
  :ret map?)

(s/fdef stats-all
  :args (s/cat :db ::specs/boundary
               :date-map (s/keys :req-un [::specs/start-date ::specs/end-date]))
  :ret (s/coll-of map?))
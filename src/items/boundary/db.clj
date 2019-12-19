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
  (last-file-time [db])
  (apb-ip [db])
  (units [db])
  (users [db]))

(extend-protocol QueryDatabase
  duct.database.sql.Boundary
  (units [{db :spec}]
    (get-units db))
  (users [{db :spec}]
    (get-users db))
  (apb-ip [{db :spec}]
    (get-ipad-ip db))
  (last-file-time [{db :spec}]
    (:last_file_time (get-last-file-time db))))

(defprotocol InsertDatabase
  (insert-table-record [db m])
  (insert-last-file-time [db m]))

(extend-protocol InsertDatabase
  duct.database.sql.Boundary
  (insert-table-record [{db :spec} m]
    (let [result (insert-table! db m)]
      (if-let [id (val (ffirst result))]
        {:id id}
        (log (logger) :error :items.db/insert-table-record m))))
  (insert-last-file-time [{db :spec} m]
    (insert-last-time! db m)))

(defprotocol ItemsDatabase
  (items-period-record [db m])
  (items-by-id [db m])
  (stats-all [db m]))

(extend-protocol ItemsDatabase
  duct.database.sql.Boundary
  (items-period-record [{db :spec} m]
    (get-items-period-record db m))
  (items-by-id [{db :spec} m]
    (get-items-by-id db m))
  (stats-all [{db :spec} m]
    (get-stats-all db m)))

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
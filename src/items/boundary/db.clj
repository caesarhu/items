(ns items.boundary.db
  (:require [hugsql.core :as hugsql]
            [integrant.repl.state :refer [config system]]
            duct.database.sql)
  (:import java.sql.SQLException))

(defn items-db [] (:duct.database.sql/hikaricp system))

(hugsql/def-db-fns "sql/queries.sql")

(defprotocol QueryDatabase
  (units [db])
  (users [db]))

(extend-protocol QueryDatabase
  duct.database.sql.Boundary
  (units [{db :spec}]
    (get-units db))
  (users [{db :spec}]
    (get-users db)))

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
          {:errors ["Failed to add record." table record]}))
      (catch SQLException ex
        {:errors [(format "Record not added due to %s" (.getMessage ex)) table record]}))))
(ns items.boundary.db
  (:require [hugsql.core :as hugsql]
            [integrant.repl.state :refer [config system]]
            duct.database.sql))

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
    (let [column-names (map name (keys record))
          column-vals (vals record)]
      (insert-table! db {:table table :column-names column-names :column-vals column-vals}))))
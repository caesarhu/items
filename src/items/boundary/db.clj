(ns items.boundary.db
  (:require [hugsql.core :as hugsql]
            [integrant.repl.state :refer [config system]]
            duct.database.sql))

(defn items-db [] (:duct.database.sql/hikaricp system))

(hugsql/def-db-fns "sql/queries.sql")

(defprotocol ToolDatabase
  (units [db])
  (users [db]))

(extend-protocol ToolDatabase
  duct.database.sql.Boundary
  (units [{db :spec}]
    (get-units db))
  (users [{db :spec}]
    (get-users db)))

(defprotocol InsertDatabase
  (units [db])
  (users [db]))
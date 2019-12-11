(ns items.boundary.db
  (:require [hugsql.core :as hugsql]
            [integrant.repl.state :refer [config system]]
            duct.database.sql))

(defn items-db [] (:duct.database.sql/hikaricp system))

(hugsql/def-db-fns "sql/queries.sql")

(defprotocol UnitDatabase
  (units [db]))

(extend-protocol UnitDatabase
  duct.database.sql.Boundary
  (units [{db :spec}]
    (get-units db)))

(defprotocol UserDatabase
  (users [db]))

(extend-protocol UserDatabase
  duct.database.sql.Boundary
  (users [db]
    (get-users db)))
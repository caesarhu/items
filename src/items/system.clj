(ns items.system
  (:require
    [integrant.core :as ig]
    [integrant.repl.state :refer [config system]]))

(defn logger []
  (when system
    (val (ig/find-derived-1 system :duct.logger/timbre))))

(defn items-db []
  (when system
    (val (ig/find-derived-1 system :duct.database.sql/hikaricp))))
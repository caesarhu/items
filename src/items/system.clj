(ns items.system
  (:require
    [integrant.core :as ig]
    [integrant.repl.state :refer [config system]]))

(defn parameter [k]
  (when system
    (val (ig/find-derived-1 system k))))

(defn logger []
  (parameter :duct.logger/timbre))

(defn items-db []
  (parameter :duct.database.sql/hikaricp))

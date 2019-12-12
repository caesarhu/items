(ns items.system
  (:require
    [integrant.core :as ig]
    [integrant.repl.state :refer [config system]]
    [datoteka.core :as fs]))

(defmethod ig/init-key :items.json-path [_ path]
  (if (fs/directory? path)
    path
    (throw (ex-info (str "Json directory not exists: " path) {:path path}))))

(defmethod ig/init-key :items.csv-path [_ path]
  (if (fs/directory? path)
    path
    (throw (ex-info (str "CSV directory not exists: " path) {:path path}))))

(defn parameter [k]
  (when system
    (val (ig/find-derived-1 system k))))

(defn logger []
  (parameter :duct.logger/timbre))

(defn items-db []
  (parameter :duct.database.sql/hikaricp))

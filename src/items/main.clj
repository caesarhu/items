(ns items.main
  (:gen-class)
  (:require
    [duct.core :as duct]
    [items.system]
    [items.json-record]
    [items.items-mail]))

(duct/load-hierarchy)

(defn -main [& args]
  (let [keys     (or (duct/parse-keys args) [:duct/daemon])
        profiles [:duct.profile/prod]]
    (-> (duct/resource "items/config.edn")
        (duct/read-config)
        (duct/exec-config profiles keys))))

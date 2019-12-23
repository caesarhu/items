(ns items.main
  (:gen-class)
  (:require [duct.core :as duct]
            [integrant.core :as ig]
            [integrant.repl :refer [clear halt go init prep reset]]
            [integrant.repl.state :refer [config system]]
            [items.system :refer [logger items-db json-path csv-path]]
            [items.items-mail :as mail]
            [items.json-record :as json]))

(duct/load-hierarchy)

(defn -main [& args]
  (let [keys     (or (duct/parse-keys args) [:duct/daemon])
        profiles [:duct.profile/prod]]
    (-> (duct/resource "items/config.edn")
        (duct/read-config)
        (duct/exec-config profiles keys))))

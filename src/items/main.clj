(ns items.main
  (:gen-class)
  (:require [duct.core :as duct]
            [integrant.core :as ig]
            [integrant.repl :refer [clear halt go init prep reset]]
            [integrant.repl.state :refer [config system]]
            [items.system :refer [logger items-db parameter json-path csv-path update-log-timestamp-opts]]
            [items.items-mail :as mail]
            [items.json-record :as json]))

(duct/load-hierarchy)

(def run-map
  #{:items/send-items-daily
    :items/items-to-db})

(defn run-fn [run-key]
  (case run-key
    :items/send-items-daily (mail/send-items-daily)
    :items/items-to-db (json/json->db)))

(defn items-run [profiles run-keys]
  (let [read-config (fn [] (duct/read-config (duct/resource "items/config.edn")))]
    (integrant.repl/set-prep! (comp update-log-timestamp-opts #(duct/prep-config (read-config) profiles)))
    (go)
    (doall (map #(run-fn %) run-keys))
    (duct/await-daemons system)))

(defn -main [& args]
  (let [keys     (or (duct/parse-keys args) [:duct/daemon])
        profiles [:duct.profile/prod]
        run-keys (not-empty (filter #(contains? run-map %) keys))]
    (if run-keys
      (items-run profiles run-keys)
      (-> (duct/resource "items/config.edn")
          (duct/read-config)
          (duct/exec-config profiles keys)))))

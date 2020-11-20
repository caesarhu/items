(ns dev
  (:refer-clojure :exclude [test])
  (:require
    [clojure.java.io :as io]
    [clojure.repl :refer :all]
    [clojure.spec.alpha :as s]
    [clojure.tools.namespace.repl :refer [refresh]]
    [datoteka.core :as fs]
    [duct.core :as duct]
    [duct.core.repl :as duct-repl]
    [duct.logger :refer [log]]
    [eftest.runner :as eftest]
    [fipp.edn :refer [pprint]]
    [hodur-translate.core :as ht]
    [integrant.core :as ig]
    [integrant.repl :refer [clear halt go init prep reset]]
    [integrant.repl.state :refer [config system]]
    [items.boundary.db :as db]
    [items.config :refer [logger items-db json-path csv-path mail-config db-call]]
    [items.items-csv :refer [generate-stats-csv generate-detail-csv delete-stats-csv delete-detail-csv]]
    [items.items-mail :as mail :refer [send-items-all send-items-daily test-send-items-all]]
    [items.items-query :refer [query-items-period-record get-items-stat]]
    [items.json-record :as record :refer [time-json->db]]
    [items.json-spec :as jspec]
    [items.schema :as schema]
    [items.utils :as utils]
    [java-time :as jt :refer [local-date local-date-time]]
    [orchestra.spec.test :as st]
    [hodur-translate.utils :refer [pretty-str spit-code]]
    [items.boundary.auto-spec :as as]))


(duct/load-hierarchy)


(defn read-config
  []
  (duct/read-config (io/resource "items/config.edn")))


(defn test
  []
  (eftest/run-tests (eftest/find-tests "test")))


(def profiles
  [:duct.profile/dev :duct.profile/local])


(def meta-db
  (ht/init-db schema/meta-schema))


(clojure.tools.namespace.repl/set-refresh-dirs "dev/src" "src" "test")


(when (io/resource "local.clj")
  (load "local"))


(defn remove-rolling-appender
  [config]
  (update config :duct.logger/timbre
          (fn [log-config]
            (let [appenders (:appenders log-config)]
              (assoc log-config :appenders (dissoc appenders :duct.logger.timbre/rolling))))))


(integrant.repl/set-prep! (comp remove-rolling-appender
                                ;update-log-timestamp-opts
                                #(duct/prep-config (read-config) profiles)))


(st/instrument)



(ns dev
  (:refer-clojure :exclude [test])
  (:require [clojure.repl :refer :all]
            [fipp.edn :refer [pprint]]
            [clojure.tools.namespace.repl :refer [refresh]]
            [clojure.java.io :as io]
            [duct.core :as duct]
            [duct.core.repl :as duct-repl]
            [eftest.runner :as eftest]
            [integrant.core :as ig]
            [integrant.repl :refer [clear halt go init prep reset]]
            [integrant.repl.state :refer [config system]]
            [duct.logger :refer [log]]
            [items.config :refer [logger items-db json-path csv-path mail-config db-call]]
            [clojure.spec.alpha :as s]
            [items.boundary.db :as db]
            [datoteka.core :as fs]
            [orchestra.spec.test :as st]
            [items.utils :as utils]
            [java-time :as jt :refer [local-date local-date-time]]
            [items.json-record :as record :refer [time-json->db]]
            [items.items-query :refer [query-items-period-record get-items-stat]]
            [items.items-csv :refer [generate-stats-csv generate-detail-csv delete-stats-csv delete-detail-csv]]
            [items.items-mail :as mail :refer [send-items-all send-items-daily test-send-items-all]]
            [items.json-spec :as jspec]
            [items.schema :as schema]
            [hodur-translate.core :as ht]))

(duct/load-hierarchy)

(defn read-config []
  (duct/read-config (io/resource "items/config.edn")))

(defn test []
  (eftest/run-tests (eftest/find-tests "test")))

(def profiles
  [:duct.profile/dev :duct.profile/local])

(def meta-db
  (ht/init-db schema/meta-schema))

(clojure.tools.namespace.repl/set-refresh-dirs "dev/src" "src" "test")

(when (io/resource "local.clj")
  (load "local"))

(defn remove-rolling-appender [config]
  (update config :duct.logger/timbre
          (fn [log-config]
            (let [appenders (:appenders log-config)]
              (assoc log-config :appenders (dissoc appenders :duct.logger.timbre/rolling))))))

(integrant.repl/set-prep! (comp remove-rolling-appender
                                ;update-log-timestamp-opts
                                #(duct/prep-config (read-config) profiles)))

(st/instrument)



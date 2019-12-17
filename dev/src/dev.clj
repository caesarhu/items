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
            [items.system :refer [logger items-db parameter json-path csv-path update-log-timestamp-opts]]
            [clojure.spec.alpha :as s]
            [items.boundary.db :as db]
            [orchestra.spec.test :as st]
            [java-time :as jt :refer [local-date local-date-time]]
            [items.json-record :refer [json->db]]
            [items.items-query :refer [query-items-period-record get-items-stat]]
            [items.items-csv :refer [generate-stats-csv generate-detail-csv delete-stats-csv delete-detail-csv]]
            [items.items-mail :refer [send-csv send-items-all send-items-daily]]
            [items.json-spec :as jspec]))

(duct/load-hierarchy)

(defn read-config []
  (duct/read-config (io/resource "items/config.edn")))

(defn test []
  (eftest/run-tests (eftest/find-tests "test")))

(def profiles
  [:duct.profile/dev :duct.profile/local])

(clojure.tools.namespace.repl/set-refresh-dirs "dev/src" "src" "test")

(when (io/resource "local.clj")
  (load "local"))

(integrant.repl/set-prep! (comp update-log-timestamp-opts #(duct/prep-config (read-config) profiles)))

(st/instrument)

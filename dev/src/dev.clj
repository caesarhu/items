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
            [items.system :refer [logger items-db parameter json-path csv-path]]
            [clojure.spec.alpha :as s]
            [items.boundary.db :as db]
            [orchestra.spec.test :as st])
  (:import
    java.util.Locale
    java.util.TimeZone))

(duct/load-hierarchy)

(def time-style
  {:timestamp-opts
   {:pattern "yyyy-MM-dd HH:mm:ss"
    :locale (java.util.Locale. "zh_TW")
    :timezone (java.util.TimeZone/getTimeZone "Asia/Taipei")}})

(defn update-log-timestamp-opts [config]
  (update config :duct.logger/timbre
          (fn [log-config]
            (merge log-config time-style))))

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

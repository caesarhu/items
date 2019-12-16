(ns items.system
  (:require
    [integrant.core :as ig]
    [integrant.repl.state :refer [config system]]
    [taoensso.timbre :as timbre]
    [taoensso.timbre.appenders.3rd-party.rolling :as rolling]
    [duct.logger :as logger]
    [datoteka.core :as fs])
  (:import
    java.util.Locale
    java.util.TimeZone))

(defn parameter [k]
  (when system
    (val (ig/find-derived-1 system k))))

(defn logger []
  (parameter :duct.logger/timbre))

(defn items-db []
  (parameter :duct.database.sql/hikaricp))

(defn json-path []
  (parameter [:duct/const :items/json-path]))

(defn csv-path []
  (parameter [:duct/const :items/csv-path]))

(defn mail-config []
  (parameter [:duct/const :items/mail-config]))

(def time-style
  {:timestamp-opts
   {:pattern "yyyy-MM-dd HH:mm:ss"
    :locale (java.util.Locale. "zh_TW")
    :timezone (java.util.TimeZone/getTimeZone "Asia/Taipei")}})

(defn update-log-timestamp-opts [config]
  (update config :duct.logger/timbre
          (fn [log-config]
            (merge log-config time-style))))

(defmethod ig/init-key :duct.logger.timbre/rolling [_ options]
  (-> (rolling/rolling-appender options)
      (merge (select-keys options [:min-level]))))
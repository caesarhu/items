(ns items.config
  (:require
    [duct.logger :as lg]
    [integrant.core :as ig]
    [clojure.java.io :as io]
    [taoensso.timbre.appenders.3rd-party.rolling :as rolling]
    [hodur-translate.core :as ht])
  (:import
    (java.util
      Locale
      TimeZone)))


(defonce system-config (atom nil))


(defn logger
  []
  (:logger @system-config))


(defn items-db
  []
  (:database @system-config))


(defn json-path
  []
  (:json-path @system-config))


(defn csv-path
  []
  (:csv-path @system-config))


(defn mail-config
  []
  (:mail-config @system-config))


(defn log
  ([level event]
   (lg/log (logger) level event))
  ([level event data]
   (lg/log (logger) level event data)))


(defn db-call
  [f & more]
  (apply f (items-db) more))

(defn read-schema [path]
  (->> path
       io/resource
       slurp
       read-string))

(defn meta-db []
  (:meta-db @system-config))

(defn refresh-db []
  (let [schema-path (:schema-path @system-config)
        meta-db (ht/init-db (read-schema schema-path))]
    (swap! system-config assoc :meta-db meta-db)))

(def time-style
  {:timestamp-opts
   {:pattern "yyyy-MM-dd HH:mm:ss"
    :locale (java.util.Locale. "zh_TW")
    :timezone (java.util.TimeZone/getTimeZone "Asia/Taipei")}})


(defmethod ig/init-key :duct.logger.timbre/rolling [_ options]
  (-> (rolling/rolling-appender options)
      (merge (select-keys options [:min-level]))))


(defmethod ig/init-key :items.system [_ options]
  (let [schema-path (:schema-path options)
        meta-db (ht/init-db (read-schema schema-path))
        new-options (assoc options :meta-db meta-db)]
    (reset! system-config new-options)
    new-options))


(defmethod ig/init-key :items.timestamp-opts [_ options]
  (let [{:keys [pattern locale timezone]} options]
    {:pattern pattern
     :locale (java.util.Locale. locale)
     :timezone (java.util.TimeZone/getTimeZone timezone)}))

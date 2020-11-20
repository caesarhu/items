(ns items.config
  (:require
    [duct.logger :as lg]
    [integrant.core :as ig]
    [taoensso.timbre.appenders.3rd-party.rolling :as rolling])
  (:import
    (java.util
      Locale
      TimeZone)))


(def items-system (atom nil))


(defn logger
  []
  (:logger @items-system))


(defn items-db
  []
  (:database @items-system))


(defn json-path
  []
  (:json-path @items-system))


(defn csv-path
  []
  (:csv-path @items-system))


(defn mail-config
  []
  (:mail-config @items-system))


(defn log
  ([level event]
   (lg/log (logger) level event))
  ([level event data]
   (lg/log (logger) level event data)))


(defn db-call
  [f & more]
  (apply f (items-db) more))


(def time-style
  {:timestamp-opts
   {:pattern "yyyy-MM-dd HH:mm:ss"
    :locale (java.util.Locale. "zh_TW")
    :timezone (java.util.TimeZone/getTimeZone "Asia/Taipei")}})


(defmethod ig/init-key :duct.logger.timbre/rolling [_ options]
  (-> (rolling/rolling-appender options)
      (merge (select-keys options [:min-level]))))


(defmethod ig/init-key :items.system [_ options]
  (reset! items-system options)
  options)


(defmethod ig/init-key :items.timestamp-opts [_ options]
  (let [{:keys [pattern locale timezone]} options]
    {:pattern pattern
     :locale (java.util.Locale. locale)
     :timezone (java.util.TimeZone/getTimeZone timezone)}))

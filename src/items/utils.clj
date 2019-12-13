(ns items.utils
  (:require
    [datoteka.core :as fs]
    [duct.logger :refer [log]]
    [items.system :refer [logger items-db]]
    [java-time :as jt]))

(defn str->int [int-str]
  (try
    (Long/parseLong int-str)
    (catch Exception ex
      (log (logger) :error "Convert string to int Error: " int-str)
      0)))

(defn head-number [s]
  (str->int (re-find #"\d+" s)))

(defn file-time [file]
  (-> (.lastModified file)
      jt/instant
      jt/fixed-clock
      jt/local-date-time))

(defn day-between? [start-date end-date day]
  (let [start (jt/min start-date end-date)
        end (jt/max start-date end-date)]
    (not (or (jt/before? day start)
             (jt/after? day end)))))

(defn json-files
  ([path ext]
   (if (and (fs/exists? path)
            (fs/directory? path)
            (fs/readable? path))
     (fs/list-dir path ext)
     (log (logger) :error (str "json path directory invalie:" path))))
  ([path]
   (json-files path "*.json")))

(defn json-file [path]
  (if (and (fs/exists? path)
           (fs/readable? path)
           (fs/regular-file? path))
    (fs/file path)
    (log (logger) :error (str "json file path invalie:" path))))

(defn file-date-between
  ([file start end]
   (let [file-date (jt/local-date (file-time file))]
     (day-between? start end file-date)))
  ([file one-day]
   (file-date-between file one-day one-day)))

(defn file-xf
  ([start-date end-date]
   (comp
     (map json-file)
     (filter #(file-date-between % start-date end-date))
     (filter #(> (.length %) 0))))
  ([one-date]
   (file-xf one-date one-date))
  ([]
   (comp
     (map json-file)
     (filter #(> (.length %) 0)))))

(defn get-json-files
  ([path start-date end-date]
   (let [file-paths (json-files path)]
     (transduce (file-xf start-date end-date) conj [] file-paths)))
  ([path one-date]
   (get-json-files path one-date one-date))
  ([path]
   (let [file-paths (json-files path)]
     (transduce (file-xf) conj [] file-paths))))

(defn remove-space [in-str]
  (when (string? in-str)
    (apply str (filter #(not= % (char \space)) (seq in-str)))))

(ns items.utils
  (:require
    [datoteka.core :as fs]
    [duct.logger :refer [log]]
    [items.system :refer [logger items-db]]
    [java-time :as jt]))

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
     (log (logger) :error :items.utils/json-files path)))
  ([path]
   (json-files path "*.json")))

(defn json-file [path]
  (if (and (fs/exists? path)
           (fs/readable? path)
           (fs/regular-file? path))
    (fs/file path)
    (log (logger) :error :items.utils/json-file path)))

(defn time-file-xf [last-time]
  (comp
    (map json-file)
    (filter some?)
    (filter #(> (.length %) 0))
    (filter #(jt/after? (file-time %) last-time))))

(defn after-time-json-files [path last-time]
  (let [file-paths (json-files path)]
    (transduce (time-file-xf last-time) conj [] file-paths)))

(defn remove-space [in-str]
  (when (string? in-str)
    (apply str (filter #(not= % (char \space)) (seq in-str)))))

(defn str->int [int-str]
  (try
    (Long/parseLong (remove-space int-str))
    (catch Exception ex
      (log (logger) :error "Convert string to int Error: " int-str)
      0)))

(defn head-number [s]
  (str->int (re-find #"\d+" s)))

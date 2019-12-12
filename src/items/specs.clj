(ns items.specs
  (:require
    [clojure.spec.alpha :as s]
    [java-time :as jt]
    duct.database.sql))

(defn boundary? [obj]
  (instance? duct.database.sql.Boundary obj))

(s/def ::boundary boundary?)
(s/def ::date jt/local-date?)
(s/def ::start-date jt/local-date?)
(s/def ::end-date jt/local-date?)
(s/def ::date-time jt/local-date-time?)
(s/def ::table string?)
(s/def ::column-names (s/coll-of string?))
(s/def ::column-vals (s/coll-of any?))
(s/def ::id pos-int?)
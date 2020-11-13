;;; Hodur Engine origin schema
(ns items.schema
  (:require
    [hodur-engine.core :as hodur]))

(def engine-schema
  '[^{:lacinia/tag true
      :datomic/tag true
      :spec/tag true}
    default

    items
    [^{:type Integer
       :datomic/unique :db.unique/identity} id
     ^String file
     ^DateTime file-time
     ^String carry-way
     ^DateTime check-time
     ^String flight
     ^String work-unit
     ^String work-sub-unit
     ^String check-police
     ^String process-way
     ^String check-sign
     ^{:type String
       :optional true} passenger-sign
     ^{:type String
       :optional true} trader-sign
     ^String ip
     ^{:type String
       :optional true} memo]])

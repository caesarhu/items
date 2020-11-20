(ns items.boundary.auto-spec
  (:require
    [spec-tools.data-spec]))

(def item-people
  {:items-id clojure.core/pos-int?,
   :id clojure.core/pos-int?,
   :kind clojure.core/string?,
   :people clojure.core/integer?,
   :piece clojure.core/integer?})


(def spec-item-people
  (spec-tools.data-spec/spec ::item-people item-people))


(def item-list
  {:object clojure.core/string?,
   :items-id clojure.core/pos-int?,
   :id clojure.core/pos-int?,
   :kind clojure.core/string?,
   :subkind clojure.core/string?})


(def spec-item-list (spec-tools.data-spec/spec ::item-list item-list))


(def units
  {:unit clojure.core/string?,
   :id clojure.core/pos-int?,
   (spec-tools.data-spec/opt :subunit)
   (spec-tools.data-spec/maybe clojure.core/string?)})


(def spec-units (spec-tools.data-spec/spec ::units units))


(def mail-list
  {:email clojure.core/string?,
   :id clojure.core/pos-int?,
   :unit clojure.core/string?,
   (spec-tools.data-spec/opt :memo)
   (spec-tools.data-spec/maybe clojure.core/string?),
   (spec-tools.data-spec/opt :position)
   (spec-tools.data-spec/maybe clojure.core/string?),
   (spec-tools.data-spec/opt :name)
   (spec-tools.data-spec/maybe clojure.core/string?),
   (spec-tools.data-spec/opt :subunit)
   (spec-tools.data-spec/maybe clojure.core/string?)})


(def spec-mail-list (spec-tools.data-spec/spec ::mail-list mail-list))


(def items
  {:file-time java-time/local-date-time?,
   (spec-tools.data-spec/opt :trader-sign)
   (spec-tools.data-spec/maybe clojure.core/string?),
   :unit clojure.core/string?,
   :check-sign clojure.core/string?,
   :file clojure.core/string?,
   :process clojure.core/string?,
   (spec-tools.data-spec/opt :flight)
   (spec-tools.data-spec/maybe clojure.core/string?),
   :check-time java-time/local-date-time?,
   :id clojure.core/pos-int?,
   (spec-tools.data-spec/opt :ip)
   (spec-tools.data-spec/maybe clojure.core/string?),
   :carry clojure.core/string?,
   (spec-tools.data-spec/opt :passenger-sign)
   (spec-tools.data-spec/maybe clojure.core/string?),
   (spec-tools.data-spec/opt :subunit)
   (spec-tools.data-spec/maybe clojure.core/string?),
   :police clojure.core/string?,
   (spec-tools.data-spec/opt :memo)
   (spec-tools.data-spec/maybe clojure.core/string?)})


(def spec-items (spec-tools.data-spec/spec ::items items))


(def last-time
  {:id clojure.core/pos-int?,
   :success clojure.core/integer?,
   :file-time java-time/local-date-time?,
   :total clojure.core/integer?,
   :fail clojure.core/integer?})


(def spec-last-time (spec-tools.data-spec/spec ::last-time last-time))


(def all-list
  {:item clojure.core/string?,
   :id clojure.core/pos-int?,
   :items-id clojure.core/pos-int?,
   :quantity clojure.core/integer?})


(def spec-all-list (spec-tools.data-spec/spec ::all-list all-list))


(def ipad
  {:unit clojure.core/string?,
   :ip clojure.core/string?,
   :id clojure.core/pos-int?,
   :ipad-name clojure.core/string?,
   (spec-tools.data-spec/opt :subunit)
   (spec-tools.data-spec/maybe clojure.core/string?)})


(def spec-ipad (spec-tools.data-spec/spec ::ipad ipad))



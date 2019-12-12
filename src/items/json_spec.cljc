(ns items.json-spec
  (:require
    [clojure.spec.alpha :as s]
    [java-time :as jt]))

(def items-db-fields
  [:單位 :子單位 :航空貨運業者簽章 :處理情形 :查獲人簽章 :員警姓名 :所有備註
   :原始檔 :旅客簽章 :查獲時間 :班次 :攜帶方式 :IP])

(def detail-fields
  [:單位 :子單位 :員警姓名 :查獲時間 :班次 :攜帶方式 :處理情形 :項目清單 :項目人數 :所有項目數量 :所有備註
   :原始檔 :查獲人簽章 :旅客簽章 :航空貨運業者簽章 :IP])

(def stat-fields
  [:開始日期 :結束日期 :單位 :子單位 :員警姓名 :種類 :類別 :合計])

(def bug-transfrom-fields
  {"臺中局" "臺中所"
   "花蓮局" "花蓮所"})

(def IP-regex #"^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$")

(s/def ::勤務單位 string?)
(s/def ::航空貨運業者簽章 string?)
(s/def ::項目人數 (s/coll-of string?))
(s/def ::處理情形 string?)
(s/def ::查獲人簽章 string?)
(s/def ::所有項目數量 map?)
(s/def ::所有備註 string?)
(s/def ::員警姓名 string?)
(s/def ::日期 string?)
(s/def ::旅客簽章 string?)
(s/def ::時間 string?)
(s/def ::班次 string?)
(s/def ::項目清單 (s/coll-of string?))
(s/def ::攜帶方式 string?)
(s/def ::查獲時間 jt/local-date-time?)
(s/def ::原始檔 string?)
(s/def ::單位 string?)
(s/def ::子單位 string?)
(s/def ::種類 string?)
(s/def ::類別 string?)
(s/def ::物品 (s/nilable string?))
(s/def ::IP (s/and string? #(re-matches IP-regex %)))

(s/def ::json-log
  (s/keys :req-un [::攜帶方式 ::日期 ::時間 ::班次 ::勤務單位 ::員警姓名
                   ::處理情形 ::查獲人簽章 ::項目清單 ::項目人數]
          :opt-un [::旅客簽章 ::航空貨運業者簽章 ::所有備註 ::所有項目數量 ::IP]))

(s/def ::items-record
  (s/keys :req-un [::攜帶方式 ::查獲時間 ::原始檔 ::班次 ::單位 ::子單位 ::員警姓名
                   ::處理情形 ::查獲人簽章 ::所有備註]
          :opt-un [::旅客簽章 ::航空貨運業者簽章 ::IP]))

(s/def ::items_id pos-int?)
(s/def ::件數 (complement neg?))
(s/def ::人數 (complement neg?))
(s/def ::項目 string?)
(s/def ::數量 (complement neg?))


(s/def ::item-spec
  (s/keys :req-un [::items_id ::種類 ::類別]
          :opt-un [::物品]))

(s/def ::people-spec
  (s/keys :req-un [::items_id ::種類 ::件數 ::人數]))

(s/def ::all-list-spec
  (s/keys :req-un [::items_id ::項目 ::數量]))

;;; date and time parse spec

(s/def ::date-spec
  (s/coll-of pos-int? :count 3))

(s/def ::time-spec
  (s/coll-of (complement neg?)))
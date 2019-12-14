(ns items.items-mail
  (:require
    [integrant.core :as ig]
    [postal.core :refer [send-message]]
    [java-time :as jt]
    [duct.logger :refer [log]]
    [items.system :refer [logger items-db json-path mail-config]]
    [items.boundary.db :as db]
    [datoteka.core :as fs]
    [items.items-csv :refer [generate-unit-name generate-stats-name generate-detail-name
                             generate-detail-csv generate-stats-csv delete-stats-csv delete-detail-csv]]
    [items.json-record :refer [json->db]]))

(def email-re #"[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")

(defn valid-email? [email-str]
  (when (string? email-str)
    (re-matches email-re email-str)))

(defn filter-email [coll]
  (filter #(valid-email? (:email %)) coll))

(defn mail-data [subject to-email file-path-1 file-path-2]
  (let [file-1 (fs/file file-path-1)
        file-2 (fs/file file-path-2)
        filename-1 (.getName file-1)
        filename-2 (.getName file-2)]
    {:from "system@dns.apb.gov.tw"
     :to to-email
     :subject subject
     :body [{:type "text/plain; charset=utf-8"
             :content "系統於每日凌晨3時自動寄送前1日危險(安)物品資料，每週二凌晨3時自動寄送前1週(上週二至週一)危險(安)物品資料，請勿直接回信，如有問題請聯絡勤指中心資訊室 736-2222。"}
            ;;;; supports both dispositions:
            {:type :attachment
             :file-name filename-1
             :content-type "text/x-csv; charset=utf-8"
             :content file-1}
            {:type :attachment
             :file-name filename-2
             :content-type "text/x-csv; charset=utf-8"
             :content file-2}]}))

(defn send-csv [subject to-email file-path-1 file-path-2]
  (if (and (fs/exists? file-path-1)
           (fs/exists? file-path-2))
    (try
      (System/setProperty "mail.mime.splitlongparameters" "false")
      ;; 附件中文檔名必須如此設定，才能讓所有mail client正確識別
      (let [result (send-message (mail-config) (mail-data subject to-email file-path-1 file-path-2))]
        (log (logger) :info "send-message result:" result))
      (catch Exception ex
        (log (logger) :error (.getMessage ex))))
    (log (logger) :error "csv file not exist! " file-path-1)))

(defn get-email-list []
  (let [list (db/users (items-db))]
    (filter-email list)))

(defn mail-items
  ([start-date end-date]
   (let [email-list (get-email-list)]
     (for [unit email-list]
       (let [{:keys [is_whole email]} unit
             unit-name (if is_whole
                         (generate-unit-name start-date end-date)
                         (generate-unit-name start-date end-date unit))
             detail-path (if is_whole
                           (generate-detail-name start-date end-date)
                           (generate-detail-name start-date end-date unit))
             stats-path (if is_whole
                          (generate-stats-name start-date end-date)
                          (generate-stats-name start-date end-date unit))
             subject (str "查獲危險(安)物品登錄資料-" unit-name)]
         (send-csv subject email detail-path stats-path)))))
  ([one-date]
   (mail-items one-date one-date)))

(defn send-items-all
  ([start-date end-date]
   (let [yesterday (jt/minus (jt/local-date) (jt/days 1))]
     (doall (json->db yesterday))
     (doall (json->db (jt/local-date)))
     (doall (generate-detail-csv start-date end-date))
     (doall (generate-stats-csv start-date end-date))
     (doall (mail-items start-date end-date))
     (doall (delete-stats-csv start-date end-date))
     (doall (delete-detail-csv start-date end-date))))
  ([one-date]
   (send-items-all one-date one-date))
  ([]
   (let [yesterday (jt/minus (jt/local-date) (jt/days 1))]
     (send-items-all yesterday))))

(defn send-items-week []
  (let [today (jt/local-date)
        last-week {:start-date (jt/minus today (jt/days 7)) :end-date (jt/minus today (jt/days 1))}]
    (when (jt/tuesday? today)
      (send-items-all (:start-date last-week) (:end-date last-week)))))

(defn send-items-daily []
  (send-items-all)
  (send-items-week))

(defmethod ig/init-key :items.items-mail/send-items-daily
  [_ _]
  (generate-stats-csv (jt/local-date)))

(derive :items.items-mail/send-items-daily :duct/daemon)

(ns items.items-mail
  (:require
    [integrant.core :as ig]
    [postal.core :refer [send-message]]
    [integrant.repl.state :refer [config system]]
    [java-time :as jt]
    [duct.logger :refer [log]]
    [items.system :refer [logger items-db json-path mail-config]]
    [items.boundary.db :as db]
    [datoteka.core :as fs]
    [items.items-csv :refer [generate-unit-name generate-stats-name generate-detail-name
                             generate-detail-csv generate-stats-csv delete-stats-csv delete-detail-csv]]
    [items.json-record :refer [time-json->db]]))

(def email-re #"[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")

(defn valid-email? [email-str]
  (when (string? email-str)
    (re-matches email-re email-str)))

(defn filter-email [coll]
  (filter #(valid-email? (:email %)) coll))

(defn attach-mail-file [path]
  (if (and (string? path) (fs/regular-file? path))
    (let [file (fs/file path)
          file-name (.getName file)]
      {:type :attachment
       :file-name file-name
       :content-type "text/x-csv; charset=utf-8"
       :content file})
    (log (logger) :error ::attach-mail-file-fail path)))

(defn make-mail-data [from to subject content & file-paths]
  (let [mail-header {:from    from
                     :to      to
                     :subject subject}
        body-base [{:type "text/plain; charset=utf-8"
                    :content content}]
        attachments (filter some? (map attach-mail-file file-paths))
        body (into body-base attachments)]
    (assoc mail-header :body body)))

(defn send-items-mail [to subject & paths]
  (let [from "system@dns.apb.gov.tw"
        content "系統於每日凌晨3時自動寄送前1日危險(安)物品資料，每週二凌晨3時自動寄送前1週(上週二至週一)危險(安)物品資料，請勿直接回信，如有問題請聯絡勤指中心資訊室 736-2222。"
        mail-data (apply make-mail-data from to subject content paths)]
    (try
      (System/setProperty "mail.mime.splitlongparameters" "false")
      ;; 附件中文檔名必須如此設定，才能讓所有mail client正確識別
      (send-message (mail-config) mail-data)
      (log (logger) :info ::send-items-mail-success to)
      (catch Exception ex
        (log (logger) :error ::send-items-mail-fail (str to " due to: " (.getMessage ex)))))))

(defn get-email-list
  ([not-test]
   (let [list (db/users (items-db))
         email-list (filter-email list)]
     (if not-test
       email-list
       (vector (first email-list)))))
  ([]
   (get-email-list false)))

(defn mail-items
  ([start-date end-date not-test]
   (let [email-list (get-email-list not-test)]
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
             subject (str "查獲危險(安)物品登錄資料-" unit-name)
             days (jt/as (jt/period start-date end-date) :days)]
         (if (> days 7)
           (send-items-mail email subject stats-path)
           (send-items-mail email subject detail-path stats-path))))))
  ([start-date end-date]
   (mail-items start-date end-date true))
  ([one-date]
   (mail-items one-date one-date)))

(defn send-items-all
  ([start-date end-date]
   (doall (time-json->db))
   (doall (generate-detail-csv start-date end-date))
   (doall (generate-stats-csv start-date end-date))
   (doall (mail-items start-date end-date))
   (doall (delete-stats-csv start-date end-date))
   (doall (delete-detail-csv start-date end-date)))
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

(defn send-items-month []
  (let [today (jt/local-date)
        last-month-day (jt/minus today (jt/days 7))
        last-month {:start-date (jt/adjust last-month-day :first-day-of-month)
                    :end-date (jt/adjust last-month-day :last-day-of-month)}]
    (when (= today (jt/adjust today :first-day-of-month))
      (send-items-all (:start-date last-month) (:end-date last-month)))))

(defn send-items-daily []
  (send-items-all)
  (send-items-week)
  (send-items-month))

(defn test-send-items-all
  ([start-date end-date]
   (doall (time-json->db))
   (doall (generate-detail-csv start-date end-date))
   (doall (generate-stats-csv start-date end-date))
   (doall (mail-items start-date end-date false))
   (doall (delete-stats-csv start-date end-date))
   (doall (delete-detail-csv start-date end-date)))
  ([one-date]
   (test-send-items-all one-date one-date))
  ([]
   (let [yesterday (jt/minus (jt/local-date) (jt/days 1))]
     (test-send-items-all yesterday))))


(ns items.json-record
  (:require
    [integrant.core :as ig]
    [shun.interceptors :refer [make-interceptor execute]]
    [cheshire.core :refer [parse-string]]
    [items.utils :as utils :refer [head-number str->int after-time-json-files]]
    [clojure.spec.alpha :as s]
    [items.json-spec :as spec :refer [items-db-fields bug-transfrom-fields json-transfrom-keys]]
    [java-time :as jt :refer [local-date local-date-time]]
    [clojure.string :as str]
    [items.system :refer [log db-call json-path]]
    [items.boundary.db :as db]))

(defn take-n-str [n in-str]
  (->> (seq in-str)
       (take n)
       (apply str)))

(defn partition-n-str [n in-str]
  (map #(apply str %) (partition n n (repeat \space) (seq in-str))))

(defn parse-date [date-str]
  (let [[year month date] (str/split date-str #"-")
        date-str-vec [(take-n-str 4 year) (take-n-str 2 month) (take-n-str 2 date)]
        date-vec (map str->int date-str-vec)]
    (when (apply local-date date-vec)
      date-vec)))

(defn parse-time [time-str]
  (let [time-str-vec (if (re-find #"[:：]" time-str)
                       (str/split time-str #"[:.：]")
                       (partition-n-str 2 time-str))
        time-vec (map str->int (take 3 time-str-vec))]
    (if (s/valid? ::spec/time-spec time-vec)
      time-vec
      (do
        (log :error ::parse-time-fail time-str)
        [0]))))

(defn make-carry-time [j-map]
  (try
    (let [{:keys [日期 時間]} j-map
          date-vec (parse-date 日期)
          time-vec (parse-time 時間)]
      (apply local-date-time (concat date-vec time-vec)))
    (catch Exception ex
      (log :error ::make-carry-time-fail (.getMessage ex)))))

(defn switch-unit [j-map]
  (let [raw-unit (:勤務單位 j-map)
        [_ 單位 子單位] (re-find #"(^.{4})(.*)" raw-unit)]
    {:單位 單位 :子單位 子單位}))

(defn bug-unit-transform [j-map]
  (let [sub-unit (:子單位 j-map)
        good-sub-unit (get bug-transfrom-fields sub-unit)]
    (if good-sub-unit
      (assoc j-map :子單位 good-sub-unit)
      j-map)))

(defn keys-transform [j-map]
  (let [target-keys (filter #(contains? json-transfrom-keys %) (keys j-map))
        target-v (map (fn [k]
                        (hash-map (get json-transfrom-keys k)
                                  (get j-map k)))
                      target-keys)]
    (apply merge j-map target-v)))

(defn parse-item [raw-item items_id]
  (let [[kind sub_kind item] (str/split raw-item #"-")
        result {:items_id items_id :種類 kind :類別 (or sub_kind "") :物品 (or item "")}]
    (if (s/valid? ::spec/item-spec result)
      result
      (log :error ::parse-item-fail (s/explain-data ::spec/item-spec result)))))

(defn parse-people [raw-people items_id]
  (let [[kind piece people] (str/split raw-people #"-")
        result {:items_id items_id :種類 kind :件數 (head-number piece) :人數 (head-number people)}]
    (if (s/valid? ::spec/people-spec result)
      result
      (log :error ::parse-people-fail (s/explain-data ::spec/people-spec result)))))

(defn parse-all-list [list items_id]
  (let [list-name (name (key list))
        list-val (str->int (val list))
        result {:items_id items_id :項目 list-name :數量 list-val}]
    (if (s/valid? ::spec/all-list-spec result)
      result
      (log :error ::parse-all-list-fail (s/explain-data ::spec/all-list-spec result)))))

(defn insert-table-row [table row]
  (try
    (let [column-names (map name (keys row))
          column-vals (vals row)
          id-map (db-call db/insert-table-record {:table table :column-names column-names :column-vals column-vals})
          id (:id id-map)]
      id)
    (catch Exception ex
      (log :error ::insert-table-row-fail (.getMessage ex)))))

(defn insert-items-record [j-map]
  (let [record (select-keys j-map items-db-fields)
        items_id (insert-table-row "items" record)]
    (if (pos-int? items_id)
      (do
        (log :info ::insert-items-record-success (:原始檔 j-map))
        (assoc j-map :items_id items_id))
      (log :error ::insert-items-record-fail (:原始檔 j-map)))))

(defn calc-item-people [j-map]
  (let [{:keys [項目清單 項目人數 items_id]} j-map
        item-list (not-empty (filter some? (map #(parse-item % items_id) 項目清單)))
        item-people (not-empty (filter some? (map #(parse-people % items_id) 項目人數)))
        kind-count (frequencies (map :種類 item-list))
        calc-people (map (fn [m]
                           (let [kind (key m)
                                 count (val m)
                                 xf (comp (filter #(= kind (:種類 %))) (map :人數))
                                 people (transduce xf + item-people)]
                                 ;people (reduce + 0 (map :人數 (filter #(= kind (:種類 %)) item-people)))]
                             {:items_id items_id :種類 kind :件數 count :人數 people}))
                         kind-count)]
    calc-people))

(defn insert-items-list [j-map]
  (let [{:keys [項目清單 項目人數 items_id 所有項目數量]} j-map
        item-list (not-empty (filter some? (map #(parse-item % items_id) 項目清單)))
        item-people (calc-item-people j-map)
        all-list (not-empty (filter some? (map #(parse-all-list % items_id) 所有項目數量)))]
    (when (pos-int? items_id)
      (do
        (doall (map (fn [item]
                      (insert-table-row "item_list" item))
                    item-list))
        (doall (map (fn [people]
                      (insert-table-row "item_people" people))
                    item-people))
        (doall (map (fn [list]
                      (insert-table-row "all_list" list))
                    all-list))
        j-map))))

(def ^:private last-file-time* (atom nil))

(def json-interceptors
  [{:name ::exceptions
    :error (fn [ctx]
             (let [error (:error ctx)
                   data (ex-data error)]
               (log :error ::json-interceptors data)
               (-> ctx
                   (dissoc :error)
                   (assoc :response nil))))}
   (make-interceptor (fn [file]
                       (let [json-str (slurp file)
                             ftime (utils/file-time file)]
                         (reset! last-file-time* (jt/max ftime @last-file-time*))
                         (assoc (parse-string json-str true) :原始檔 (.getName file)
                                                             :檔案時間 ftime))))
   (make-interceptor (fn [j-map]
                       (if (s/valid? ::spec/json-log j-map)
                         j-map
                         (log :error ::json-file-parsed-invalid (s/explain-data ::spec/json-log j-map)))))
   (make-interceptor #(update % :勤務單位 utils/remove-space))
   (make-interceptor #(assoc % :查獲時間 (make-carry-time %)))
   (make-interceptor #(merge % (switch-unit %)))
   (make-interceptor bug-unit-transform)
   (make-interceptor keys-transform)
   (make-interceptor (fn [j-map]
                       (if (s/valid? ::spec/items-record j-map)
                         j-map
                         (log :error ::items-record-parsed-invalid (s/explain-data ::spec/items-record j-map)))))
   (make-interceptor insert-items-record)
   (make-interceptor insert-items-list)
   (fn [_]
     :success)])

(defn json->record [file]
  (try
    (execute json-interceptors file)
    (catch Exception e
      (log :error ::json->record (str "caught exception: " " file: " file)))))

(defn time-json->db []
  (reset! last-file-time* (db-call db/last-file-time))
  (let [last-file-time @last-file-time*
        files (after-time-json-files (json-path) last-file-time)
        result (filter some? (map json->record files))
        total (count result)
        success (count (filter #(= :success %) result))
        report {:file_time @last-file-time* :total total :success success :fail (- total success)}]
    (log :info ::time-json->db-result report)
    (when (jt/after? @last-file-time* last-file-time)
      (doall (db-call db/insert-last-file-time report)))
    report))

(defmethod ig/init-key :items/items-to-db [_ options]
  (let [{:keys [system environment]} options]
    (when (= :production environment)
      (time-json->db))))
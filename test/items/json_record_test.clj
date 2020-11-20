(ns items.json-record-test
  (:require
    [clojure.test :refer :all]
    [items.json-record :as json]
    [orchestra.spec.test :as st]))


(use-fixtures
  :once
  (fn [f]
    (st/instrument)
    (f)))


(deftest take-n-str-test
  (testing "take-n-str test"
    (is (= "12"
           (json/take-n-str 2 "1234567890")))
    (is (= "1234"
           (json/take-n-str 4 "1234567890")))))


(deftest partition-n-str-test
  (testing "partition-n-str test"
    (is (= '("12" "34" "56" "78" "90")
           (json/partition-n-str 2 "1234567890")))
    (is (= '("1234" "5678" "90  ")
           (json/partition-n-str 4 "1234567890")))))


(deftest parse-date-test
  (testing "parse-date test"
    (is (= '(2019 12 15)
           (json/parse-date "2019-12-150277")))
    (is (= '(2019 12 15)
           (json/parse-date "2019-12-15")))
    (is (= '(2019 12 31)
           (json/parse-date "2019-12-31")))))


(deftest parse-time-test
  (testing "parse-time test"
    (is (= '(9 18)
           (json/parse-time "0918")))
    (is (= '(17 46 20)
           (json/parse-time "174620")))
    (is (= '(17 46 20)
           (json/parse-time "17:46：20.355")))))

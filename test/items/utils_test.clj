(ns items.utils-test
  (:require
    [clojure.test :refer :all]
    [items.utils :as utils]
    [orchestra.spec.test :as st]))


(use-fixtures
  :once
  (fn [f]
    (st/instrument)
    (f)))


(deftest str->int-test
  (testing "str->int-test test"
    (is (= 1234
           (utils/str->int "1234")))
    (is (= 0
           (utils/str->int "")))))

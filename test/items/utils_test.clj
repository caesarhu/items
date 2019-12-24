(ns items.utils-test
  (:require
    [clojure.test :refer :all]
    [orchestra.spec.test :as st]
    [items.utils :as utils]))

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
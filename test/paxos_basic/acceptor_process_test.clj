(ns paxos-basic.acceptor-process-test
  (:require [clojure.test :refer :all]
            [paxos-basic.acceptor-process :refer :all]))

(testing "Acceptor process"
  (testing "resets its min proposal when a higher proposal num comes with a prepare request"
    (is false))
  (testing "keeps its min proposal when a prepare request with a lower proposal num comes"
    (is false))
  (testing "sets its accepted proposal and value when an appropriate accept request comes"
    (is false)))
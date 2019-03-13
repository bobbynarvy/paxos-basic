(ns paxos-basic.acceptor-process-test
  (:require [clojure.test :refer :all]
            [paxos-basic.acceptor-process :refer :all]))

(testing "Acceptor process"
  (testing "resets its min proposal when a higher proposal num comes with a prepare request"
    (respond-prepare-req {:prop-num "some-server/1"})
    (is (= "some-server/1" (@state :min-prop))))
  (testing "keeps its min proposal when a prepare request with a lower proposal num comes"
    (respond-prepare-req {:prop-num "some-other-server/0"})
    (is (= "some-server/1" (@state :min-prop))))
  (testing "sets its accepted proposal and value when an appropriate accept request comes"
    (respond-accept-req {:prop-num "some-server/1"
                         :value "test value"})
    (is (= "some-server/1" (@state :accepted-prop)))
    (is (= "test value" (@state :accepted-value)))))

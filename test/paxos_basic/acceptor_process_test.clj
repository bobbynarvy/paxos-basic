(ns paxos-basic.acceptor-process-test
  (:require [clojure.test :refer :all]
            [paxos-basic.acceptor-process :refer :all]))

(testing "Acceptor process"
  (testing "resets its min proposal when a higher proposal num comes with a prepare request"
    (respond-prepare-req {:prop-round 1 :server-id :server-1}
                         (fn [p1 p2] ()) ;; do nothing 
                         :test-proposer)
    (is (= 1 (@state :min-prop-round)))
    (is (= :server-1 (@state :min-prop-server-id))))
  (testing "keeps its min proposal when a prepare request with a lower proposal num comes"
    (respond-prepare-req {:prop-round 0 :server-id :server-0}
                         (fn [p1 p2] ()) ;; do nothing 
                         :test-proposer-2)
    (is (= 1 (@state :min-prop-round)))
    (is (= :server-1 (@state :min-prop-server-id))))
  (testing "sets its accepted proposal and value when an appropriate accept request comes"
    (respond-accept-req {:prop-round 1 
                         :server-id :server-1
                         :value "test value"}
                         (fn [p1 p2] ()) ;; do nothing
                         :test-proposer)
    (is (= 1 (@state :accepted-prop)))
    (is (= "test value" (@state :accepted-value)))))
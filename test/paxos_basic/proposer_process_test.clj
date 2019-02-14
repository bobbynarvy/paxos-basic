(ns paxos-basic.proposer-process-test
  (:require [clojure.test :refer :all]
            [paxos-basic.proposer-process :refer :all]))

(init-proposer :test-id :test-value)

(testing "Proposer process"
  (testing "accepts new acceptor"
    (add-acceptor :acceptor-test)
    (is (= [:acceptor-test] @acceptors))
    (is (= 1 (@state :acceptor-cnt)))
    (dotimes [n 4]
      (add-acceptor (keyword (str "acceptor-test-" n))))) ;; add more acceptors to make 5 acceptors
  (testing "can receive a prepare response"
    (recv-prepare-resp {:accepted-value nil
                        :accepted-prop nil
                        :message-id (@state :message-id)})
    (is (= 1 (count (@state :responses)))))
  (testing "ignores a response that is not meant for the current prepare phase"
    (recv-prepare-resp {:accepted-value nil
                        :accepted-prop nil
                        :message-id "something-else"})
    (is (= 1 (count (@state :responses)))))
  (testing "sets its value to the response value of the prepare request with the highest proposal number"
    (recv-prepare-resp {:accepted-value nil
                        :accepted-prop nil
                        :message-id (@state :message-id)})
    (recv-prepare-resp {:accepted-value "value!"
                        :accepted-prop 1
                        :message-id (@state :message-id)})
    (is (= 3 (count (@state :responses))))
    (is (= "value!" (@state :value)))))

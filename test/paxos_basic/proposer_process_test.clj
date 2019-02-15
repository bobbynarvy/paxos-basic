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
  ;; Here, let's just assume that the proposer has successfully sent its prepare requests.
  ;; In this scenario, the proposer will be receiving 3 response. The one with the hight accepted
  ;; proposal has a value of "value!" which the proposer thould update its value to
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
    (def old-message-id (@state :meesage-id)) ;; cache old message id before it is changed
    (recv-prepare-resp {:accepted-value nil
                        :accepted-prop nil
                        :message-id (@state :message-id)})
    (recv-prepare-resp {:accepted-value "value!"
                        :accepted-prop 1
                        :message-id (@state :message-id)})
    (is (= "value!" (@state :value))))
  (testing "associates new accept request information"
    (is (not= old-message-id (@state :message-id)))
    (is (empty? (@state :responses)))
    (is (= :accept (@state :phase)))))
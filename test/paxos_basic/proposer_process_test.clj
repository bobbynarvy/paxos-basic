(ns paxos-basic.proposer-process-test
  (:require [clojure.test :refer :all]
            [paxos-basic.proposer-process :refer :all]))

(init-proposer :test-id :test-value)

(testing "Proposer process"
  ;; To start off, let's add 5 acceptors. This means that at least 3 responses
  ;; to any requests are needed to trigger the proposer to do its next step.
  (testing "accepts new acceptor"
    (add-acceptor :acceptor-test)
    (is (= [:acceptor-test] @acceptors))
    (is (= 1 (@state :acceptor-cnt)))
    (dotimes [n 4]
      (add-acceptor (keyword (str "acceptor-test-" n))))) ;; add more acceptors to make 5 acceptors
  ;; Here, let's just assume that the proposer has successfully sent its prepare requests.
  ;; In this scenario, the proposer will be receiving 3 responses. The one with the highest accepted
  ;; proposal has a value of "value!" which the proposer should update its value to
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
    (def old-message-id (@state :message-id)) ;; cache old message id before it is changed
    (recv-prepare-resp {:accepted-value nil
                        :accepted-prop nil
                        :message-id (@state :message-id)})
    (recv-prepare-resp {:accepted-value "value!"
                        :accepted-prop 1
                        :message-id (@state :message-id)})
    (is (= "value!" (@state :value))))
  (testing "associates new accept request information"
    (def new-message-id (@state :message-id))
    (is (not= old-message-id new-message-id))
    (is (empty? (@state :responses)))
    (is (= :accept (@state :phase))))
  (testing "ignores a response that arrived late"
    (recv-prepare-resp {:accepted-value nil
                        :accepted-prop nil
                        :message-id old-message-id})
    (is (empty? (@state :responses)))
    ;; check that the late response did not trigger a change
    ;; in the prepare request
    (is (= new-message-id (@state :message-id))))
  ;; Here, let's assume that accept requests have been successfully sent.
  ;; In this scenario, the proposer will be receiving 3 responses with 
  ;; one result having a minimum proposal higher than that of the proposer's,
  ;; thus the proposer's request failing.
  ;;
  ;; The first accept response is the one with the higher accepted proposal number
  (recv-accept-resp {:accepted-prop 1
                     :message-id (@state :message-id)})
  (recv-accept-resp {:accepted-prop 0
                     :message-id (@state :message-id)})
  (def last-accept-resp (recv-accept-resp {:accepted-prop 0
                                            :message-id (@state :message-id)}))
  (testing "recognizes that its prepare request has not been accepted"
    (is (false? last-accept-resp)))
  (testing "is reset when its value is not accepted"
    (is (= 1 (@state :prop-num)))
    (is (empty? (@state :responses)))))
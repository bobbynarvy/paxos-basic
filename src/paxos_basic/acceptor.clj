(ns paxos-basic.acceptor
  (require [paxos-basic.proposal-num :as prop-num]))

(defn init-acceptor
  "Create the initial acceptor state"
  []
  {:min-prop "0/0"
   :accepted-prop nil
   :accepted-value nil})

(defn create-prepare-response
  "Create a response to a prepare
  request from a proposer"
  [state prepare-req]
  (if (not= 1 (prop-num/compare-prop-nums (state :min-prop) (prepare-req :prop-num)))
    (assoc state
           :min-prop (prepare-req :prop-num))
    state))

(defn get-prepare-response
  "Send the prepare response
  to the proposer"
  [state]
  (select-keys state [:accepted-prop :accepted-value]))

(defn create-accept-response
  "Create a response to an accept
  request from a proposer"
  [state accept-req]
  (if (not= 1 (prop-num/compare-prop-nums (state :min-prop) (accept-req :prop-num)))
    (assoc state
           :min-prop (accept-req :prop-num)
           :accepted-prop (accept-req :prop-num)
           :accepted-value (accept-req :value))  
    state))

(defn get-accept-response
  "Send the accept response
  to the proposer"
  [state]
  (select-keys state [:accepted-prop]))

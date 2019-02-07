(ns paxos-basic.acceptor)

(defn- compare-min-prop-to-proposer-prop
  "Compares the value of the acceptor's
  minimum proposal to that of the proposer.

  Returns 1 when it is greater, 0 when equal,
  and -1 when less."
  [{min-prop-round :min-prop-round min-prop-server-id :min-prop-server-id}
   {proposer-prop-round :prop-round proposer-server-id :server-id}]
  (cond
    (> min-prop-round proposer-prop-round) 1
    (= min-prop-round proposer-prop-round) (cond
                                             (> min-prop-server-id proposer-server-id) 1
                                             (= min-prop-server-id proposer-server-id) 0
                                             :else -1)
    :else -1))

(defn init-acceptor
  "Create the initial acceptor state"
  []
  {:min-prop-round 0
   :min-prop-server-id 0
   :accepted-prop nil
   :accepted-value nil})

(defn create-prepare-response
  "Create a response to a prepare
  request from a proposer"
  [state prepare-req]
  (if (not= 1 (compare-min-prop-to-proposer-prop state prepare-req))
    (assoc state
           :min-prop-round (prepare-req :prop-round)
           :min-prop-server-id (prepare-req :server-id))
    state))

(defn send-prepare-response
  "Send the prepare response
  to the proposer"
  [state sender-fn]
  (sender-fn (select-keys state [:accepted-prop :accepted-value])))

(defn create-accept-response
  "Create a response to an accept
  request from a proposer"
  [state accept-req]
  (if (not= -1 (compare-min-prop-to-proposer-prop state accept-req))
    (assoc state
           :min-prop-round (accept-req :prop-round)
           :min-prop-server-id (accept-req :server-id)
           :accepted-prop (accept-req :prop-round)
           :accepted-value (accept-req :value))
    state))

(defn send-accept-response
  "Send the accept response
  to the proposer"
  [state send-fn]
  (send-fn (state :accepted-prop)))

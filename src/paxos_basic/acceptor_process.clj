(ns paxos-basic.acceptor-process
  (require [paxos-basic.acceptor :as acceptor]))

(def state (atom (acceptor/init-acceptor)))

(defn respond-prepare-req
  "Respond to a prepare request"
  [prepare-req messenger-fn proposer]
  (swap! state acceptor/create-prepare-response prepare-req)
  (acceptor/send-prepare-response @state
                                  (partial messenger-fn proposer)))

(defn respond-accept-req
  "Respond to an accept request"
  [accept-req messenger-fn proposer]
  (swap! state acceptor/create-accept-response accept-req)
  (acceptor/send-accept-response @state
                                 (partial messenger-fn proposer)))

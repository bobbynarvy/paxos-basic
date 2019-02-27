(ns paxos-basic.acceptor-process
  (require [paxos-basic.acceptor :as acceptor]))

(def state (atom (acceptor/init-acceptor)))

(defn respond-prepare-req
  "Respond to a prepare request"
  [prepare-req]
  (swap! state acceptor/create-prepare-response prepare-req)
  (acceptor/get-prepare-response @state))

(defn respond-accept-req
  "Respond to an accept request"
  [accept-req]
  (swap! state acceptor/create-accept-response accept-req)
  (acceptor/get-accept-response @state))

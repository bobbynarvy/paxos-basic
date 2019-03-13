(ns paxos-basic.proposer-process
  (require [paxos-basic.proposer :as proposer]))

(def state (atom {}))

(def acceptors (atom []))

(defn init-proposer
  "Initialize the proposer state"
  [value]
  (reset! state (proposer/init-proposer value)))

(defn add-acceptor
  "Add an acceptor for later user"
  [acceptor]
  (swap! state #(update % :acceptor-cnt inc))
  (swap! acceptors conj acceptor))

(defn request-prepare
  "Broadcast prepare request
  to all acceptors"
  [messenger-fn]
  (doseq [acceptor @acceptors]
    (proposer/send-prepare-request @state
                                   (partial messenger-fn acceptor))))

(defn recv-prepare-resp
  "Receive a prepare response. Once majority
  of responses are received, check and replace
  proposer value if needed."
  [prepare-resp]
  (swap! state proposer/add-response prepare-resp)
  (when (proposer/has-majority-responses? @state)
    (swap! state proposer/prepare->accept-req)))

(defn request-accept
  "Broadcast accept request
  to all acceptors"
  [messenger-fn]
  (doseq [acceptor @acceptors]
    (proposer/send-accept-request @state
                                  (partial messenger-fn acceptor))))

(defn recv-accept-resp
  "Receive an accept response. Once majority
  of responses are received, check whether
  the proposer's value has been chosen.
  
  Returns a boolean value when a majority of
  responses have been received, the value of which 
  depends on whether the proposer's value has been
  chosen."
  [accept-resp]
  (swap! state proposer/add-response accept-resp)
  (when (proposer/has-majority-responses? @state)
    (let [chosen? (proposer/value-is-chosen? state)]
      (when (not chosen?)
        (swap! state proposer/reset-proposer))
      chosen?)))

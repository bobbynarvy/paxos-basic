(ns paxos-basic.acceptor-server
  (require [paxos-basic.socket-server :as server]))

(defn acceptor-handler
  [data]
  ;; Parse the data into a request
  ;; Determine the type of request
  ;; Create the response based on the request type
  ;; Convert and return the response to a string
  )

(defn serve
  "Start a process that listens to
  requests coming from proposers."
  [port]
  (server/serve-peristent port acceptor-handler))

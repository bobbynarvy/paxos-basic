(ns paxos-basic.acceptor-server
  (require [paxos-basic.socket-server :as server]
           [paxos-basic.message :as message]))

(defn request-handler
  "Gets the handler based on the
  type of request"
  [request]
  ())

(defn acceptor-handler
  "Returns a response from an acceptor
  based on a request from a proposer"
  [str-data]
  (let [request (message/str->map str-data)         ;; Parse the data into a request
        handler (request-handler request)           ;; Get the handler based on the type of request
        response (handler request)                  ;; Create the acceptor response
        str-response (message/map->str response)]   ;; Convert and return the response to a string
    str-response))

(defn serve
  "Start a process that listens to
  requests coming from proposers."
  [port]
  (server/serve-peristent port acceptor-handler))

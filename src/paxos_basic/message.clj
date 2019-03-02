;; This namespace is dedicated to utility functions
;; related to messages that are exchanged between
;; proposers and acceptors in the basic paxos algorithm
;;
;; A message string is simply a string that contains
;; key-value pairs separated by a new line. It is formatted
;; similarly to an HTML message. For example, a message string
;; to an acceptor would look like:
;;
;; PHASE: Accept
;; SERVER-ID: 123
;; PROP-NUM: 1
;; VALUE: Test Value
;;
;; A message map is simply a Clojure map containing
;; information about the current state in the algorithm
(ns paxos-basic.message)

(defn str->map
  "Converts a message string to a message map"
  [message]
  ())

(defn map->str
  "Converts a message map to a message string"
  [str]
  ())

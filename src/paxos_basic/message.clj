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
;; SERVER-ID: "SOME-SERVER-ID"
;; PROP-NUM: 1
;; VALUE: Test Value
;;
;; A message map is simply a Clojure map containing
;; information about the current state in the algorithm
(ns paxos-basic.message
  (require [clojure.string :as string]))

(defn- str-pair->key-val
  [key-val-str]
  (-> (string/split key-val-str #" ")
      ((fn [ss] {(->> (first ss)
                      (string/lower-case)
                      (butlast)
                      (apply str)
                      (keyword))
                 (-> (string/join " " (rest ss))
                     (#(cond (number? (read-string %)) (read-string %) ;; return number 
                             (= "NIL" %) nil                           ;; return nil
                             :else %)                                  ;; return string
                      ))}))))

(defn- key-val->str-pair
  [[key val]]
  (str ((comp string/upper-case name) key)
       ": "
       (#(if (nil? %) "NIL" %) val)))

(defn str->map
  "Converts a sequence of message strings into
  a message map"
  [msg-strs]
  (->> (map str-pair->key-val msg-strs)
       (apply merge)))

(defn map->str
  "Converts a message map to a message string"
  [msg-map]
  (->> (into [] msg-map)
       (map key-val->str-pair)
       (string/join "\n")))

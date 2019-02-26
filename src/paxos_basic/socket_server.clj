;;; This file implements a low-level TCP server using sockets.
;;; Code taken from the Clojure cookbook:
;;; https://github.com/clojure-cookbook/clojure-cookbook/blob/master/05_network-io/5-10_tcp-server.asciidoc
(ns paxos-basic.socket-server
  (require [clojure.java.io :as io])
  (import [java.net ServerSocket]))

(defn receive
  "Read a line of textual data from the given socket"
  [socket]
  (.readLine (io/reader socket)))

(defn send
  "Send the given string message out over the given socket"
  [socket msg]
  (let [writer (io/writer socket)]
      (.write writer msg)
      (.flush writer)))

(defn serve [port handler]
  (with-open [server-sock (ServerSocket. port)
              sock (.accept server-sock)]
    (let [msg-in (receive sock)
          msg-out (handler msg-in)]
      (send sock msg-out))))

(defn serve-persistent [port handler]
  (let [running (atom true)]
    (future
      (with-open [server-sock (ServerSocket. port)]
        (while @running
          (with-open [sock (.accept server-sock)]
            (let [msg-in (receive sock)
                  msg-out (handler msg-in)]
              (send sock msg-out))))))
    running))

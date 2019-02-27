;; Paxos server

;; Server workflow:
;; - Create the following buffers:
;;    - A vector for all incoming messages
;;    - A channel for all proposer messages
;;    - A channel for all outgoing proposer messages
;;    - A channel for all acceptor messages
;;    - A channel for all outgoing acceptor messages
;; - Create the following state holders (i.e. atoms):
;;    - Proposer state
;;    - Acceptor state
;; - Create a thread in which a socket will be opened
;;    - Create a socket from which messages will be queued and distributed
;;    - When socket receives a message, it routes the message to the proper queue
;; - Create an acceptor block. This block:
;;    - Listens for messages meant for the acceptor.
;;    - Contains state about the accepted proposal and value
;; - On main thread, wait for user command to initiate proposal
;;    - When user initiates proposal, start proposer workflow
;;    - When proposer workflow is finished, log it

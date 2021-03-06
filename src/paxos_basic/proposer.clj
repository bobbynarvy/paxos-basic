(ns paxos-basic.proposer
  (require [paxos-basic.proposal-num :as prop-num]))

(defn- create-uuid [] (.toString (java.util.UUID/randomUUID)))

(defn init-proposer
  "Create the initial proposer state"
  [value]
  {:message-id (create-uuid)
   :prop-num (prop-num/create-prop-num (create-uuid))
   :value value
   :phase "Prepare"
   :acceptor-cnt 0
   :responses []})

(defn send-prepare-request
  "Send the prepare request
  to acceptors"
  [state sender-fn]
  (sender-fn (select-keys state [:prop-num])))

(defn get-response-majority-size
  "Get the number of response
  needed to qualify for a majority"
  [state]
  ((comp inc #(quot % 2)) (state :acceptor-cnt)))

(defn add-response
  "Add a response to the state,
  filtering out responses that do not
  correspond to the current message ID"
  [state response]
  (if (= (state :message-id) (response :message-id))
    (assoc state :responses (conj (state :responses) response))
    state))

(defn has-majority-responses?
  "Returns true only when the exact majority of
  responses have been received, not more, not less."
  [state]
  (= (count (state :responses))
      (get-response-majority-size state)))

(defn get-highest-accepted-value
  "Get the accepted value
  from the highest accepted proposal
  among the responses received"
  [{responses :responses}]
  (when (not (empty? responses))
    (-> (sort-by :prop-num prop-num/compare-prop-nums responses)
        (last)
        :accepted-value)))

(defn prepare->accept-req
  "Transition the state from the
  prepare to the accept phase:
  
  1. Reset the value of the proposer
  with the accepted value of the
  highest accepted proposal, when available.
  2. Create a new message ID
  3. Modify :phase value
  4. Empty the :responses vector"
  [state]
  (let [new-value (get-highest-accepted-value state)] 
    (-> (if (some? new-value)
          (assoc state :value new-value)
          state)
        (assoc :message-id (create-uuid) :phase "Accept" :responses []))))

(defn send-accept-request
  "Send the accept request
  to acceptors"
  [state sender-fn]
  (sender-fn (select-keys state
                          [:prop-num :value])))

(defn value-is-chosen?
  "Check if the proposed value
  has been accepted by checking if
  there are any responses where the
  accepted proposal is greater than
  that of the proposer"
  [{responses :responses prop-num :prop-num}]
  (->> (map :accepted-prop responses)
       (filter #(> % prop-num))
       (count)
       (< 0)))

(defn reset-proposer
  "Reset the proposer:
  
  1. Incrememnt proposal number
  2. Create a new message ID
  3. Modify :phase value
  4. Empty the :responses vector"
  [state]
  (assoc state
        :prop-num (prop-num/inc-prop-num (state :prop-num))
        :message-id (create-uuid) 
        :phase "Prepare"
        :responses []))

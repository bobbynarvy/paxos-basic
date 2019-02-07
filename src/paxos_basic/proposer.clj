(ns paxos-basic.proposer)

(defn init-proposer
  "Create the initial proposer state"
  [server-id value]
  {:server-id server-id
   :prop-num 0
   :value value
   :responses []})

(defn send-prepare-request
  "Send the prepare request
  to acceptors"
  [state sender-fn]
  (sender-fn (select-keys state [:server-id :prop-num])))

(defn get-response-majority-size
  "Get the number of response
  needed to qualify for a majority"
  [state]
  ((comp inc #(quot % 2) count) (state :responses)))

(defn add-response
  "Add a response to the state"
  [state response]
  (assoc state :responses (conj (state :responses) response)))

(defn responses-enough?
  "Check whether the majority of
  responses have been received"
  [state]
  (>= (count (state :responses))
      (get-response-majority-size state)))

(defn get-highest-accepted-value
  "Get the highest accepted value
  from the high accepted proposal
  among the responses received"
  [{responses :responses}]
  (->> (filter #(contains? % :accepted-value))
       (map #(get % :accepted-value))
       (#(if (zero? (count %))
           nil
           (apply max %)))))

(defn replace-value
  "Reset the value of the proposer
  with the accepted value of the
  highest accepted proposal"
  [state]
  (let [new-value (get-highest-accepted-value state)]
    (if (some? new-value)
      (assoc state :value new-value)
      state)))

(defn send-accept-request
  "Send the accept request
  to acceptors"
  [state sender-fn]
  (sender-fn (select-keys state
                          [:server-id :prop-num :value])))

(defn value-is-chosen?
  "Check if the proposed value
  has been accepted by checking if
  there are any responses where the
  accepted proposal is greater than
  that of the proposer"
  [{responses :responses prop-num :prop-num}]
  (->> (map #(get % :accepted-prop) responses)
       (filter #(> % prop-num))
       (count)
       (< 0)))
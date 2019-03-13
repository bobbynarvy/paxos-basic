(ns paxos-basic.proposal-num
  (require [clojure.string :as string]))

(defn- str->map
  "Convert the proposal number
  from string to map form"
  [prop-num-str]
  (if (some? prop-num-str)
    (let [[server-id round-num] (string/split prop-num-str #"/")]
      {:server-id server-id :round-num (read-string round-num)})
    {:server-id "0" :round-num 0}))

(defn- map->str
  "Convert the proposal number
  from map to string form"
  [{server-id :server-id round-num :round-num}]
  (str server-id "/" round-num))

(defn create-prop-num
  "Initialize a proposal number"
  ([server-id]
   (str server-id "/" 0))
  ([server-id round-num]
   (str server-id "/" round-num)))

(defn inc-prop-num
  "Increment a proposal number"
  [prop-num-str]
  (-> (update (str->map prop-num-str) :round-num inc)
      (map->str)))

(defn compare-prop-nums
  "Compares two proposal numbers.

  Returns 0 if both are equal,
  1 if the first is greater,
  -1 if the first is less."
  [prop-num-str-a prop-num-str-b]
  (let [{srv-a :server-id rnd-a :round-num} (str->map prop-num-str-a)
        {srv-b :server-id rnd-b :round-num} (str->map prop-num-str-b)]
    (cond
      (> rnd-a rnd-b) 1
      (= rnd-a rnd-b) (cond
                        (< 0 (compare srv-a srv-b)) 1
                        (= srv-a srv-b) 0
                        :else -1)
      :else -1)))

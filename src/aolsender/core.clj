(ns aolsender.core
  (:require
   [aolsender.http :as http]
   [throttler.core :refer [throttle-chan]]
   [clojure.core.async :refer [chan >!! <!! thread]]
   ))

; ---
(def persec 3)
(def userid "544e47370cf290158766171d")
(def tuplename "RequestPacket")
(def hosturl "192.168.30.10:9191")
; ---

(def in (chan 1))
(def slow-chan (throttle-chan in persec :second))
;(def slow-chan (throttle-chan in 1 :millisecond))

(defn showThreadId []
  "Getting thread-id of this processing"
  (.getId (Thread/currentThread)))

(defn showResponse
  "Showing corespondent response."
  [res status body]
  (println (str status " " res)))

(defn postEachLine
  "Post the given data with http-kit client."
  [line]
  (http/postItem
   hosturl userid tuplename line
   (partial showResponse (clojure.string/trim-newline (str line " (" (showThreadId) ")")))))

(defn async-kicker
  "Start num-consumers threads that will consume work from the slow-chan"
  [num-consumers]
  (dotimes [_ num-consumers]
    (thread
      (while true
        (let [line (<!! slow-chan)]
          (postEachLine line))))))

(defn -main
  "Main function called via 'lein run'"
  [& args]
  (println (str "aolsender started with " persec " threads."))
  (do
    (async-kicker 8)
    (doseq [line (line-seq (java.io.BufferedReader. *in*))]
      (>!! in line))))
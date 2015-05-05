(ns aolsender.core
  (:use [clojure.pprint])
  (:require
   [aolsender.http :as http]
   [throttler.core :refer [throttle-chan]]
   [clojure.string :as string]
   [clojure.tools.cli :refer [parse-opts]]
   [clojure.core.async :refer [chan >!! <!! thread]]
   ))

(def cli-options
  ;definitions of option
  ;long option should have an exampleo in it....
  [["-t" "--tuple A" "REQUIRED: Target tuple name you want to send"
    :id :tuplename
    :default nil]
   ["-u" "--userid A" "REQUIRED: Target user name you want to send"
    :id :userid
    :default nil]
   ["-s" "--server localhost:9191" "gungnir server address"
    :id :hosturl
    :default "internal-vagrant.genn.ai:9191"]
   ["-l" "--limit 10" "Reset per millsec"
    :id :persec
    :default 10]
   ["-n" "--numthread 8" "Num of Sending threads"
    :id :numthread
    :default 8]
   ["-b" "--beacon" "For using beacon data"
    :id :beacon
    :default false]
   ["-h" "--help" "Show this help msg"]])

(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn usage [options-summary]
  (->> ["This is my program. There are many like it, but this one is mine."
        ""
        "Usage: program-name [options] action"
        ""
        "Options:"
        options-summary
        ""
        "Please refer to the manual page for more information."]
       (string/join \newline)))

(def in (chan 1))
;(def slow-chan (throttle-chan in persec :persec))
;(def slow-chan (throttle-chan in 1 ))

(def not-nil? (complement nil?))

(defn showThreadId []
  "Getting thread-id of this processing"
  (.getId (Thread/currentThread)))

(defn showResponse
  "Showing corespondent response."
  [object status body]
  (println (str status " "
                (- (System/currentTimeMillis) (:start object)) " "
                (:line object) " (" (:threadid object) ")")))

(defn postEachLine
  "Post the given data with http-kit client."
  [options line]
  (http/postItem
   (:hosturl options) (:userid options) (:tuplename options) line
   (partial showResponse {:line line
                          :threadid (showThreadId)
                          :start (System/currentTimeMillis)})))

(defn async-kicker
  "Start num-consumers threads that will consume work from the slow-chan"
  [options]
  (let [slow-chan (throttle-chan in (:persec options) :second)]
    (dotimes [_ (:numthread options)]
      (thread
        (while true
          (let [line (<!! slow-chan)]
            (postEachLine options line)))))))

(def VALID-CHARS
 (seq "abcdefghijklmnopqrstuwvxyz")
;  (map char (concat (range 48 58) ; 0-9
;                    (range 66 91) ; A-Z
;                    (range 97 123)))
  ) ; a-z

(defn random-char []
  (let [pos (rand-int (count VALID-CHARS))
        chr (nth VALID-CHARS pos)]
    (str chr)))

(defn random-str [length]
  (apply str (take length (repeatedly random-char))))

(defn -main
  "Main function called via 'lein run'"
  [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (if (:help options)
      (exit 0 (usage summary)))
    (if (nil? (:tuplename options))
      (do (println "Tuple name have to be defined using \"-t\" option")
        (exit 0 (usage summary)))
      (println "Tuple: " (:tuplename options)))
    (if (nil? (:userid options))
      (do (println "Userid have to be defined using \"-t\" option")
        (exit 0 (usage summary)))
      (println "Userid: " (:userid options)))

    (async-kicker options)

    (if :beacon
      (do (println "BEACON TST")
        (while true
          (let [A {:uid (random-char)
                   :bid (random-char)
                   :time (System/currentTimeMillis)}]
            ;(println A)
            (>!! in A)
            ))))

    (println (str "aolsender started with " (:persec options) " per sec limit."))
    ;(pprint options)(exit 1 "DEBuG")
    (do
      (doseq [line (line-seq (java.io.BufferedReader. *in*))]
        (>!! in line)))))

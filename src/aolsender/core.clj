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
  [["-u" "--urlpath /gungnir/-+-+/json" "REQUIRED: Target url path you want to send"
    :id :urlpath
    :default nil]
   ["-s" "--server localhost:7200" "gungnir server address"
    :id :hostname
    :default "internal-vagrant.genn.ai:7200"]
   ["-l" "--limit 10" "Reset per millsec"
    :id :persec
    :default 10]
   ["-n" "--numthread 8" "Num of Sending threads"
    :id :numthread
    :default 8]
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
   (:hostname options) (:urlpath options) line
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
            (postEachLine options (clojure.string/trim-newline line))))))))

(defn -main
  "Main function called via 'lein run'"
  [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (if (:help options)
      (exit 0 (usage summary)))
    (if (nil? (:urlpath options))
      (do (println "Url path have to be defined using \"-t\" option")
        (exit 0 (usage summary)))
      (println "Userid: " (:userid options)))
    (println (str "aolsender started with " (:persec options) " per sec limit."))
    ;(pprint options)(exit 1 "DEBuG")
    (do
      (async-kicker options)
      (doseq [line (line-seq (java.io.BufferedReader. *in*))]
        (>!! in line)))))

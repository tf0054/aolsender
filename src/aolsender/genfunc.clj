(ns aolsender.genfunc
  (:require [clojure.data.json :as json]))

(def intCounter (atom 0))
(declare next! range-size rand-str)

;;; Functions for generating Json string.

(defn simple []
  (let [id (next!)]
   (json/write-str {:id id :randstr (rand-str 20)})))

;;; http://bit.ly/1uqbkCp

(defn- next! [] (dec (swap! intCounter inc)))

(defn- range-size
  ([start size] (range-size start size 1))
  ([start size step]
    {:pre[(integer? size)]}
    (take size (range start (+ start (* size step)) step))))

(defn- rand-str
  ([n] (rand-str n (mapcat #(apply range-size %) [[48 10] [65 26] [97 26]])))
  ([n charseq] (apply str (map char (repeatedly n #(rand-nth charseq))))))

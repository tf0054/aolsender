(ns aolsender.http
  (:require [clojure.data.json :as json]
            [org.httpkit.client :as http]
            [clojure.string :as string]
            [taoensso.timbre :as timbre])
  )

(defmulti contentCheck class)
(defmethod contentCheck String [objContent]
  (clojure.string/trim-newline objContent))
(defmethod contentCheck :default [objContent]
  (json/write-str objContent))

(defn postItem [strBaseUrl strUid strTupleName objContent func]
  ; http://shenfeng.me/async-clojure-http-client.html
  ; http://www.markhneedham.com/blog/2013/09/26/clojure-writing-json-to-a-filereading-json-from-a-file/
  (let [strUrl (str "http://" strBaseUrl "/gungnir/v0.1/" strUid "/" strTupleName "/json")
        options {:headers {"Content-Type" "application/json"}
                 :keepalive 3000
                 :body (contentCheck objContent)
                 }]

    (println strUrl)
    (http/post strUrl options
              ;asynchronous with callback
               (fn [{:keys [status error body headers]}]
                 (if error
                   (timbre/debugf "Failed(%s), exception is %s" status error)
                   (func status body)))
               )))

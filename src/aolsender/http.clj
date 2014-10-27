(ns aolsender.http
  (:require [clojure.data.json :as json]
            [org.httpkit.client :as http]
            [taoensso.timbre :as timbre])
  )


;curl -i     -H "Content-Type: application/json" -X POST -d '{"request_pheader":{"ID":20,"Source_Ip":"172.20.4.65","Destination_Ip":"160.37.39.44","Source_Port":80,"Destination_Port":1920},"request_properties":{"Host":"host.004.jp","Request_URI":"/path/002 HTTP/1.1"}}'
;http://localhost:9191/gungnir/v0.1/track/544a65950cf28a00f105fb79/RequestPacket

(defmulti contentCheck class)
(defmethod contentCheck String [objContent]
  (json/write-str objContent))
(defmethod contentCheck :default [objContent]
  objContent)

(defn postItem [strBaseUrl strUid strTupleName objContent func]
  ; http://shenfeng.me/async-clojure-http-client.html
  ; http://www.markhneedham.com/blog/2013/09/26/clojure-writing-json-to-a-filereading-json-from-a-file/
  (let [options {:headers {"Content-type" "application/json"}
                 :body (contentCheck objContent)
                 }]

    (http/post (str "http://" strBaseUrl "/gungnir/v0.1/track/" strUid "/" strTupleName) options
              ;asynchronous with callback
               (fn [{:keys [status error body headers]}]
                 (if error
                   (timbre/debugf "Failed(%s), exception is %s" status error)
                   (func status body)))
               )))

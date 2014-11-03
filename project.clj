(defproject aolsender "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/data.json "0.2.4"]
                 [org.clojure/tools.cli "0.3.1"]
                 [com.taoensso/timbre "3.2.1"]
                 [http-kit "2.1.18"]
                 [throttler "1.0.0"]
]
  :source-paths ["src"]
  :main aolsender.core
  )
